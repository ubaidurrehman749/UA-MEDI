package com.example.ui.state

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.HealthAiRepository
import com.example.data.ai.ReportAnalysisResult
import com.example.data.ai.SymptomAssessmentInput
import com.example.data.ai.SymptomAssessmentResult
import com.example.data.education.EducationArticle
import com.example.data.local.AppLanguage
import com.example.data.local.AppointmentEntity
import com.example.data.local.HealthMateDatabase
import com.example.data.local.HealthMateRepository
import com.example.data.local.JournalEntryEntity
import com.example.data.local.MedicationEntity
import com.example.data.local.MedicationLogEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.SavedReportSummaryEntity
import com.example.data.local.UserPreferences
import com.example.util.PdfReportHelper
import com.google.firebase.ai.Chat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "HealthMateViewModel"

sealed interface AiUiState<out T> {
    data object Idle : AiUiState<Nothing>
    data object Loading : AiUiState<Nothing>
    data class Success<T>(val data: T) : AiUiState<T>
    data class Error(val message: String) : AiUiState<Nothing>
}

data class ChatMessage(
    val id: Long,
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false
)

class HealthMateViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HealthMateDatabase.getInstance(application)
    private val repository = HealthMateRepository(db.healthMateDao())
    private val prefsRepo = PreferencesRepository(application)
    private val aiRepo = HealthAiRepository()

    val preferences: StateFlow<UserPreferences> = prefsRepo.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserPreferences()
    )

    val medications: StateFlow<List<MedicationEntity>> = repository.medications.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val medicationLogs: StateFlow<List<MedicationLogEntity>> = repository.medicationLogs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val journalEntries: StateFlow<List<JournalEntryEntity>> = repository.journalEntries.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val appointments: StateFlow<List<AppointmentEntity>> = repository.appointments.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val savedReports: StateFlow<List<SavedReportSummaryEntity>> = repository.savedReports.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // AI Health Assistant Chat
    private var activeChat: Chat? = null
    private var activeChatLang: AppLanguage? = null

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatStreaming = MutableStateFlow(false)
    val isChatStreaming: StateFlow<Boolean> = _isChatStreaming.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    // Symptom Checker
    private val _symptomState = MutableStateFlow<AiUiState<SymptomAssessmentResult>>(AiUiState.Idle)
    val symptomState: StateFlow<AiUiState<SymptomAssessmentResult>> = _symptomState.asStateFlow()

    private val _localEmergencyDetected = MutableStateFlow(false)
    val localEmergencyDetected: StateFlow<Boolean> = _localEmergencyDetected.asStateFlow()

    // Medical Report Analyzer
    private val _reportBitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val reportBitmaps: StateFlow<List<Bitmap>> = _reportBitmaps.asStateFlow()

    private val _reportFileName = MutableStateFlow<String?>(null)
    val reportFileName: StateFlow<String?> = _reportFileName.asStateFlow()

    private val _reportLoadError = MutableStateFlow<String?>(null)
    val reportLoadError: StateFlow<String?> = _reportLoadError.asStateFlow()

    private val _reportAiState = MutableStateFlow<AiUiState<ReportAnalysisResult>>(AiUiState.Idle)
    val reportAiState: StateFlow<AiUiState<ReportAnalysisResult>> = _reportAiState.asStateFlow()

    // Appointment AI Prep
    private val _appointmentAiLoadingId = MutableStateFlow<Long?>(null)
    val appointmentAiLoadingId: StateFlow<Long?> = _appointmentAiLoadingId.asStateFlow()

    private val _appointmentAiError = MutableStateFlow<String?>(null)
    val appointmentAiError: StateFlow<String?> = _appointmentAiError.asStateFlow()

    // Education AI Summaries
    private val _educationSummaries = MutableStateFlow<Map<String, AiUiState<String>>>(emptyMap())
    val educationSummaries: StateFlow<Map<String, AiUiState<String>>> = _educationSummaries.asStateFlow()

    // Global status message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    // Preferences & Consent
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setDarkMode(enabled) }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val next = if (preferences.value.language == AppLanguage.ENGLISH) {
                AppLanguage.URDU
            } else {
                AppLanguage.ENGLISH
            }
            prefsRepo.setLanguage(next)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { prefsRepo.setLanguage(language) }
    }

    fun setDisplayName(name: String) {
        viewModelScope.launch {
            prefsRepo.setDisplayName(name)
            _toastMessage.value = "Profile name updated locally."
        }
    }

    fun acceptDisclaimer() {
        viewModelScope.launch { prefsRepo.setDisclaimerAccepted(true) }
    }

    fun loadSampleDemoData() {
        viewModelScope.launch {
            repository.seedSampleDemoData()
            _toastMessage.value = "Sample demo health records added."
        }
    }

    fun deleteAllUserData() {
        viewModelScope.launch {
            repository.clearAllUserData()
            _reportBitmaps.value = emptyList()
            _reportFileName.value = null
            _reportAiState.value = AiUiState.Idle
            _symptomState.value = AiUiState.Idle
            _toastMessage.value = "All local health records have been permanently deleted."
        }
    }

    // AI Health Assistant
    fun ensureChatInitialized(language: AppLanguage) {
        if (activeChat == null || activeChatLang != language) {
            activeChat = aiRepo.createChatSession(language)
            activeChatLang = language
            if (_chatMessages.value.isEmpty()) {
                val welcome = if (language == AppLanguage.URDU) {
                    "Assalam-o-Alaikum! Main HealthMate AI hoon. Aap mujh se aam sehat, alamaat ya medical terminology ke baray mein English ya Urdu mein pooch sakte hain. (Yeh aam maloomat hai, medical diagnosis nahi.)"
                } else {
                    "Hello! I'm HealthMate AI. Ask me about general health topics, everyday symptoms, preventive wellness, or medical terms in English or Urdu. (Educational guidance only — not medical diagnosis.)"
                }
                _chatMessages.value = listOf(
                    ChatMessage(id = System.currentTimeMillis(), isUser = false, text = welcome)
                )
            }
        }
    }

    fun resetChat() {
        val lang = preferences.value.language
        activeChat = aiRepo.createChatSession(lang)
        activeChatLang = lang
        _chatError.value = null
        _chatMessages.value = emptyList()
        ensureChatInitialized(lang)
    }

    fun sendChatMessage(rawText: String) {
        val text = rawText.trim()
        if (text.isBlank() || _isChatStreaming.value) return

        val lang = preferences.value.language
        ensureChatInitialized(lang)
        val chat = activeChat ?: return

        _chatError.value = null
        val userMsg = ChatMessage(id = System.currentTimeMillis(), isUser = true, text = text)
        val assistantMsgId = System.currentTimeMillis() + 1L
        val assistantPlaceholder = ChatMessage(
            id = assistantMsgId,
            isUser = false,
            text = "",
            isStreaming = true
        )
        _chatMessages.value = _chatMessages.value + userMsg + assistantPlaceholder
        _isChatStreaming.value = true

        viewModelScope.launch {
            val buffer = StringBuilder()
            try {
                withTimeout(60_000L) {
                    aiRepo.sendChatMessageStream(chat, text, lang).collect { chunk ->
                        buffer.append(chunk)
                        updateStreamingMessage(assistantMsgId, buffer.toString(), isStreaming = true)
                    }
                }
                if (buffer.isBlank()) {
                    removeMessage(assistantMsgId)
                    _chatError.value = "Assistant returned an empty response. Please try again."
                } else {
                    updateStreamingMessage(assistantMsgId, buffer.toString(), isStreaming = false)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chat timed out", e)
                if (buffer.isBlank()) removeMessage(assistantMsgId)
                else updateStreamingMessage(assistantMsgId, buffer.toString(), isStreaming = false)
                _chatError.value = "Response timed out after 60 seconds. Please try again."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Chat error", e)
                if (buffer.isBlank()) removeMessage(assistantMsgId)
                else updateStreamingMessage(assistantMsgId, buffer.toString(), isStreaming = false)
                _chatError.value = e.localizedMessage ?: "Unable to connect to HealthMate AI right now."
            } finally {
                _isChatStreaming.value = false
            }
        }
    }

    private fun updateStreamingMessage(id: Long, text: String, isStreaming: Boolean) {
        _chatMessages.value = _chatMessages.value.map { msg ->
            if (msg.id == id) msg.copy(text = text, isStreaming = isStreaming) else msg
        }
    }

    private fun removeMessage(id: Long) {
        _chatMessages.value = _chatMessages.value.filterNot { it.id == id }
    }

    // Symptom Checker
    fun checkLocalRedFlags(text: String) {
        val lower = text.lowercase()
        val redFlagKeywords = listOf(
            "chest pain", "chest pressure", "can't breathe", "cannot breathe", "shortness of breath",
            "difficulty breathing", "stroke", "slurred speech", "face droop", "unconscious",
            "fainted", "heavy bleeding", "uncontrolled bleeding", "anaphylaxis", "throat swelling",
            "seizure", "suicidal", "سینے میں درد", "سانس لینے میں دشواری", "بے ہوشی", "زیادہ خون"
        )
        _localEmergencyDetected.value = redFlagKeywords.any { lower.contains(it) }
    }

    fun runSymptomAssessment(input: SymptomAssessmentInput) {
        checkLocalRedFlags("${input.mainSymptoms} ${input.associatedSymptoms}")
        viewModelScope.launch {
            _symptomState.value = AiUiState.Loading
            val result = aiRepo.assessSymptoms(input)
            _symptomState.value = result.fold(
                onSuccess = { AiUiState.Success(it) },
                onFailure = { AiUiState.Error(it.message ?: "Assessment failed.") }
            )
        }
    }

    fun resetSymptomAssessment() {
        _symptomState.value = AiUiState.Idle
        _localEmergencyDetected.value = false
    }

    // Medical Report Analyzer
    fun loadReportFile(context: Context, uri: Uri, displayLabel: String) {
        viewModelScope.launch {
            _reportLoadError.value = null
            _reportAiState.value = AiUiState.Idle
            val res = PdfReportHelper.loadBitmapsFromUri(context, uri)
            res.fold(
                onSuccess = { bitmaps ->
                    _reportBitmaps.value = bitmaps
                    _reportFileName.value = displayLabel
                },
                onFailure = { err ->
                    _reportBitmaps.value = emptyList()
                    _reportLoadError.value = err.message ?: "Unable to open selected file."
                }
            )
        }
    }

    fun analyzeLoadedReport(optionalNotes: String) {
        val bitmaps = _reportBitmaps.value
        if (bitmaps.isEmpty()) {
            _reportLoadError.value = "Please upload a PDF or image report first."
            return
        }
        viewModelScope.launch {
            _reportAiState.value = AiUiState.Loading
            val res = aiRepo.analyzeMedicalReport(bitmaps, optionalNotes, preferences.value.language)
            _reportAiState.value = res.fold(
                onSuccess = { AiUiState.Success(it) },
                onFailure = { AiUiState.Error(it.message ?: "Report analysis failed.") }
            )
        }
    }

    fun saveCurrentReportAnalysis() {
        val state = _reportAiState.value
        if (state !is AiUiState.Success) return
        val data = state.data
        val flagged = data.extractedTests
            .filter { it.flag == "HIGH" || it.flag == "LOW" }
            .joinToString("\n") { "${it.testName}: ${it.reportedValue} (${it.flag})" }
            .ifBlank { "No High/Low flags explicitly marked." }
        viewModelScope.launch {
            repository.saveReportSummary(
                SavedReportSummaryEntity(
                    title = data.reportTitle,
                    summary = data.simpleSummary,
                    flaggedHighlights = flagged,
                    questionsForDoctor = data.questionsForDoctor.joinToString("\n") { "• $it" }
                )
            )
            _toastMessage.value = "Report summary saved locally."
        }
    }

    fun deleteSavedReport(id: Long) {
        viewModelScope.launch { repository.deleteReportSummary(id) }
    }

    // Medications
    fun saveMedication(med: MedicationEntity) {
        viewModelScope.launch {
            if (med.id == 0L) repository.saveMedication(med) else repository.updateMedication(med)
            _toastMessage.value = "Medication reminder saved."
        }
    }

    fun deleteMedication(id: Long) {
        viewModelScope.launch {
            repository.deleteMedication(id)
            _toastMessage.value = "Medication removed."
        }
    }

    fun logMedicationStatus(med: MedicationEntity, status: String) {
        viewModelScope.launch {
            repository.logMedicationAction(med, status)
            _toastMessage.value = "${med.name} marked as $status."
        }
    }

    // Journal
    fun saveJournalEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            repository.saveJournalEntry(entry)
            _toastMessage.value = "Health journal entry saved locally."
        }
    }

    fun deleteJournalEntry(id: Long) {
        viewModelScope.launch { repository.deleteJournalEntry(id) }
    }

    // Appointments
    fun saveAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            if (appointment.id == 0L) repository.saveAppointment(appointment)
            else repository.updateAppointment(appointment)
            _toastMessage.value = "Appointment preparation saved."
        }
    }

    fun deleteAppointment(id: Long) {
        viewModelScope.launch { repository.deleteAppointment(id) }
    }

    fun toggleAppointmentDocument(appointment: AppointmentEntity, item: String) {
        val currentChecked = appointment.checkedDocuments
            .split("|")
            .filter { it.isNotBlank() }
            .toMutableSet()
        if (currentChecked.contains(item)) currentChecked.remove(item) else currentChecked.add(item)
        viewModelScope.launch {
            repository.updateAppointment(
                appointment.copy(checkedDocuments = currentChecked.joinToString("|"))
            )
        }
    }

    fun generateAiQuestionsForAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            _appointmentAiLoadingId.value = appointment.id
            _appointmentAiError.value = null
            val res = aiRepo.generateAppointmentPrep(
                specialty = appointment.specialty,
                reason = appointment.reason,
                symptomNotes = appointment.symptomHistoryNotes,
                language = preferences.value.language
            )
            res.fold(
                onSuccess = { prep ->
                    val formattedQuestions = prep.questionsToAsk
                        .mapIndexed { idx, q -> "${idx + 1}. $q" }
                        .joinToString("\n")
                    val mergedDocs = (appointment.documentChecklist.split("|") + prep.suggestedDocuments)
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .joinToString("|")
                    repository.updateAppointment(
                        appointment.copy(
                            generatedQuestions = formattedQuestions,
                            documentChecklist = mergedDocs
                        )
                    )
                    _toastMessage.value = "Doctor questions & checklist updated."
                },
                onFailure = { err ->
                    _appointmentAiError.value = err.message ?: "Unable to generate questions."
                }
            )
            _appointmentAiLoadingId.value = null
        }
    }

    // Health Education AI Summary
    fun summarizeArticle(article: EducationArticle) {
        val lang = preferences.value.language
        val title = if (lang == AppLanguage.URDU) article.titleUr else article.titleEn
        val body = if (lang == AppLanguage.URDU) article.bodyUr else article.bodyEn
        viewModelScope.launch {
            _educationSummaries.value = _educationSummaries.value + (article.id to AiUiState.Loading)
            val res = aiRepo.summarizeEducationTopic(title, body, lang)
            val newState = res.fold(
                onSuccess = { AiUiState.Success(it) },
                onFailure = { AiUiState.Error(it.message ?: "Failed to summarize.") }
            )
            _educationSummaries.value = _educationSummaries.value + (article.id to newState)
        }
    }
}

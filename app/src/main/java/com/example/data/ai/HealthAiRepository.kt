package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.local.AppLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "HealthAiRepository"
private const val MODEL_NAME = "gemini-3.8-flash"
private const val AI_TIMEOUT_MS = 60_000L

data class SymptomAssessmentInput(
    val age: String,
    val biologicalSex: String,
    val mainSymptoms: String,
    val duration: String,
    val severity: Int, // 1..10
    val associatedSymptoms: String,
    val existingConditions: String,
    val currentMedications: String,
    val language: AppLanguage
)

data class SymptomAssessmentResult(
    val symptomSummary: String,
    val possibleExplanations: List<String>,
    val followUpQuestions: List<String>,
    val warningSigns: List<String>,
    val urgencyLevel: String, // "EMERGENCY", "URGENT", "ROUTINE", "SELF_CARE"
    val urgencyLabel: String,
    val urgencyExplanation: String,
    val disclaimer: String
)

data class LabTestItem(
    val testName: String,
    val reportedValue: String,
    val referenceRange: String,
    val flag: String, // "HIGH", "LOW", "NORMAL", "UNKNOWN"
    val plainExplanation: String
)

data class ReportAnalysisResult(
    val reportTitle: String,
    val simpleSummary: String,
    val extractedTests: List<LabTestItem>,
    val termsExplained: List<String>,
    val questionsForDoctor: List<String>,
    val disclaimer: String
)

data class AppointmentPrepAiResult(
    val questionsToAsk: List<String>,
    val suggestedDocuments: List<String>,
    val preparationTip: String
)

class HealthAiRepository {

    private val assistantSystemInstruction: Content by lazy {
        content {
            text(
                """
                You are HealthMate AI, an empathetic, medically safe healthcare education and wellness companion.
                MANDATORY SAFETY RULES:
                1. You are an educational and wellness support tool, NOT a licensed medical professional.
                2. NEVER claim to diagnose, cure, or rule out any disease. Always label potential causes as general possibilities for discussion with a clinician.
                3. NEVER prescribe medications, recommend specific prescription drugs, or suggest changing/stopping prescribed dosages.
                4. If the user describes emergency warning signs (chest pain/pressure, severe breathing difficulty, stroke signs like facial droop/arm weakness/slurred speech, severe allergic reaction, uncontrolled bleeding, loss of consciousness, severe trauma, suicidal ideation), IMMEDIATELY instruct them to call local emergency services (e.g., 1122 in Pakistan, 112, or 911) or go to the nearest emergency department right away. Do not delay emergency advice with excessive questions.
                5. Clearly distinguish general health education from personalized medical advice.
                6. Communicate warmly, clearly, and concisely in plain language. Support both English and Urdu accurately when requested.
                7. Do not invent clinical statistics, laboratory values, or fake medical citations.
                """.trimIndent()
            )
        }
    }

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = assistantSystemInstruction
        )
    }

    private val symptomSchema = Schema.obj(
        properties = mapOf(
            "symptomSummary" to Schema.string("Clear empathetic summary of what the user reported"),
            "possibleExplanations" to Schema.array(
                items = Schema.string(),
                description = "Non-diagnostic general possibilities labeled clearly as possibilities to discuss with a doctor"
            ),
            "followUpQuestions" to Schema.array(
                items = Schema.string(),
                description = "Helpful clarifying questions a healthcare professional might ask"
            ),
            "warningSigns" to Schema.array(
                items = Schema.string(),
                description = "Red flag symptoms that require urgent or emergency care"
            ),
            "urgencyLevel" to Schema.enumeration(
                values = listOf("EMERGENCY", "URGENT", "ROUTINE", "SELF_CARE"),
                description = "Recommended urgency tier"
            ),
            "urgencyLabel" to Schema.string("Human-readable urgency heading in the requested language"),
            "urgencyExplanation" to Schema.string("Brief rationale for the urgency tier without guaranteeing safety"),
            "disclaimer" to Schema.string("Short reminder that this is educational info, not a diagnosis")
        )
    )

    private val symptomModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = symptomSchema
            },
            systemInstruction = assistantSystemInstruction
        )
    }

    private val reportSchema = Schema.obj(
        properties = mapOf(
            "reportTitle" to Schema.string("Detected report type or title, e.g., Complete Blood Count"),
            "simpleSummary" to Schema.string("Plain-language overview of what the report covers"),
            "extractedTests" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "testName" to Schema.string("Name of lab test or measurement"),
                        "reportedValue" to Schema.string("Exact value and unit seen in report, or 'Not clearly visible'"),
                        "referenceRange" to Schema.string("Reference range shown in report, or 'Not listed'"),
                        "flag" to Schema.enumeration(listOf("HIGH", "LOW", "NORMAL", "UNKNOWN")),
                        "plainExplanation" to Schema.string("General educational explanation of what this test measures")
                    )
                ),
                description = "Extracted test results visible in the uploaded report"
            ),
            "termsExplained" to Schema.array(
                items = Schema.string(),
                description = "Medical terminology from the report explained in simple language"
            ),
            "questionsForDoctor" to Schema.array(
                items = Schema.string(),
                description = "Thoughtful questions the patient can ask their doctor"
            ),
            "disclaimer" to Schema.string("Clear reminder that lab ranges vary and a clinician must interpret results")
        )
    )

    private val reportModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = reportSchema
            },
            systemInstruction = assistantSystemInstruction
        )
    }

    private val appointmentSchema = Schema.obj(
        properties = mapOf(
            "questionsToAsk" to Schema.array(
                items = Schema.string(),
                description = "5-7 focused, practical questions to ask the doctor during consultation"
            ),
            "suggestedDocuments" to Schema.array(
                items = Schema.string(),
                description = "Checklist items of reports, logs, or records to bring"
            ),
            "preparationTip" to Schema.string("Short supportive tip for making the most of the visit")
        )
    )

    private val appointmentModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = appointmentSchema
            },
            systemInstruction = assistantSystemInstruction
        )
    }

    private val educationModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = assistantSystemInstruction
        )
    }

    fun createChatSession(language: AppLanguage): Chat {
        val greeting = if (language == AppLanguage.URDU) {
            "Assalam-o-Alaikum! Main HealthMate AI hoon. Main aap ko aam sehat ki maloomat aur medical terminology samajhne mein madad kar sakta hoon. Yaad rakhein, main doctor ka mutabadil nahi hoon."
        } else {
            "Hello! I'm HealthMate AI. I can help explain general health topics, symptoms, and medical terms in simple language. Please remember I provide educational support, not medical diagnoses."
        }
        return chatModel.startChat(
            history = listOf(
                content("model") { text(greeting) }
            )
        )
    }

    fun sendChatMessageStream(
        chat: Chat,
        userMessage: String,
        language: AppLanguage
    ): Flow<String> {
        val promptWithLang = if (language == AppLanguage.URDU) {
            "Respond empathetically in clear Urdu (or Roman Urdu/Urdu script matching the user's style) while keeping medical safety rules:\n\n$userMessage"
        } else {
            "Respond empathetically in clear, simple English while keeping medical safety rules:\n\n$userMessage"
        }
        return chat.sendMessageStream(promptWithLang)
            .map { chunk -> chunk.text.orEmpty() }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                Log.e(TAG, "Chat streaming failed", throwable)
                throw throwable
            }
    }

    suspend fun assessSymptoms(input: SymptomAssessmentInput): Result<SymptomAssessmentResult> {
        return try {
            val langDirective = if (input.language == AppLanguage.URDU) {
                "Write all text fields in clear Urdu (اردو)."
            } else {
                "Write all text fields in clear, empathetic English."
            }
            val prompt = """
                $langDirective
                Perform a structured, non-diagnostic educational symptom overview for the following user-entered details:
                - Age: ${input.age.ifBlank { "Not specified" }}
                - Biological Sex: ${input.biologicalSex.ifBlank { "Not specified" }}
                - Main Symptoms: ${input.mainSymptoms}
                - Duration: ${input.duration.ifBlank { "Not specified" }}
                - Severity (1-10): ${input.severity}
                - Associated Symptoms: ${input.associatedSymptoms.ifBlank { "None reported" }}
                - Existing Conditions: ${input.existingConditions.ifBlank { "None reported" }}
                - Current Medications: ${input.currentMedications.ifBlank { "None reported" }}

                Strict requirements:
                - Never diagnose a disease or guarantee a symptom is harmless.
                - Label possibilities clearly as possibilities (e.g., "Possible factors a clinician may consider include...").
                - Do NOT prescribe medications or dosages.
                - If any potentially dangerous or emergency symptoms are present (e.g., chest pain, shortness of breath, fainting, severe headache, neurological deficit, uncontrolled bleeding), set urgencyLevel to "EMERGENCY".
            """.trimIndent()

            val response = withTimeout(AI_TIMEOUT_MS) {
                symptomModel.generateContent(prompt)
            }
            val jsonStr = response.text ?: error("Empty response from AI model.")
            val json = JSONObject(jsonStr)
            Result.success(
                SymptomAssessmentResult(
                    symptomSummary = json.getString("symptomSummary"),
                    possibleExplanations = json.getJSONArray("possibleExplanations").toStringList(),
                    followUpQuestions = json.getJSONArray("followUpQuestions").toStringList(),
                    warningSigns = json.getJSONArray("warningSigns").toStringList(),
                    urgencyLevel = json.getString("urgencyLevel"),
                    urgencyLabel = json.getString("urgencyLabel"),
                    urgencyExplanation = json.getString("urgencyExplanation"),
                    disclaimer = json.getString("disclaimer")
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Symptom assessment timed out", e)
            Result.failure(Exception("Assessment timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Symptom assessment failed", e)
            Result.failure(Exception(e.localizedMessage ?: "Unable to complete symptom assessment."))
        }
    }

    suspend fun analyzeMedicalReport(
        bitmaps: List<Bitmap>,
        optionalNotes: String,
        language: AppLanguage
    ): Result<ReportAnalysisResult> {
        if (bitmaps.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please select a report image or PDF first."))
        }
        return try {
            val langDirective = if (language == AppLanguage.URDU) {
                "Provide simpleSummary, plainExplanation, termsExplained, questionsForDoctor, and disclaimer in clear Urdu (اردو). Keep exact test names and lab values accurate."
            } else {
                "Provide all explanations and summaries in clear, accessible English."
            }
            val promptContent = content {
                text(
                    """
                    $langDirective
                    Analyze the attached medical or laboratory report page(s).
                    User context note: ${optionalNotes.ifBlank { "None provided" }}

                    Strict rules:
                    - Only extract test names, values, and reference ranges that are ACTUALLY visible in the document. Never invent lab numbers.
                    - Highlight values marked High or Low by the lab (or outside the printed reference range).
                    - Explain terminology simply without diagnosing any disease.
                    - Clearly state in the disclaimer that reference ranges vary between laboratories and a qualified healthcare professional must interpret results in clinical context.
                    """.trimIndent()
                )
                bitmaps.take(4).forEach { bmp ->
                    image(bmp)
                }
            }

            val response = withTimeout(AI_TIMEOUT_MS) {
                reportModel.generateContent(promptContent)
            }
            val jsonStr = response.text ?: error("Empty response from AI model.")
            val json = JSONObject(jsonStr)
            val testsJson = json.getJSONArray("extractedTests")
            val tests = (0 until testsJson.length()).map { idx ->
                val obj = testsJson.getJSONObject(idx)
                LabTestItem(
                    testName = obj.getString("testName"),
                    reportedValue = obj.getString("reportedValue"),
                    referenceRange = obj.getString("referenceRange"),
                    flag = obj.getString("flag"),
                    plainExplanation = obj.getString("plainExplanation")
                )
            }
            Result.success(
                ReportAnalysisResult(
                    reportTitle = json.getString("reportTitle"),
                    simpleSummary = json.getString("simpleSummary"),
                    extractedTests = tests,
                    termsExplained = json.getJSONArray("termsExplained").toStringList(),
                    questionsForDoctor = json.getJSONArray("questionsForDoctor").toStringList(),
                    disclaimer = json.getString("disclaimer")
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Report analysis timed out", e)
            Result.failure(Exception("Report analysis timed out after 60 seconds. Try uploading fewer pages or a clearer image."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Report analysis failed", e)
            Result.failure(Exception(e.localizedMessage ?: "Failed to analyze medical report."))
        }
    }

    suspend fun generateAppointmentPrep(
        specialty: String,
        reason: String,
        symptomNotes: String,
        language: AppLanguage
    ): Result<AppointmentPrepAiResult> {
        return try {
            val langDirective = if (language == AppLanguage.URDU) {
                "Respond in clear Urdu (اردو)."
            } else {
                "Respond in clear English."
            }
            val prompt = """
                $langDirective
                Help a patient prepare for an upcoming consultation:
                - Doctor Specialty: ${specialty.ifBlank { "General Physician" }}
                - Reason for Consultation: $reason
                - Symptom / Wellness Notes: ${symptomNotes.ifBlank { "None added" }}

                Generate practical questions to ask the doctor and a concise checklist of documents/records to bring.
                Do not prescribe treatments or diagnose.
            """.trimIndent()

            val response = withTimeout(AI_TIMEOUT_MS) {
                appointmentModel.generateContent(prompt)
            }
            val jsonStr = response.text ?: error("Empty response from AI model.")
            val json = JSONObject(jsonStr)
            Result.success(
                AppointmentPrepAiResult(
                    questionsToAsk = json.getJSONArray("questionsToAsk").toStringList(),
                    suggestedDocuments = json.getJSONArray("suggestedDocuments").toStringList(),
                    preparationTip = json.getString("preparationTip")
                )
            )
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Request timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Appointment prep AI failed", e)
            Result.failure(Exception(e.localizedMessage ?: "Failed to generate appointment questions."))
        }
    }

    suspend fun summarizeEducationTopic(
        topicTitle: String,
        articleBody: String,
        language: AppLanguage
    ): Result<String> {
        return try {
            val langDirective = if (language == AppLanguage.URDU) {
                "Summarize in simple, warm Urdu (اردو) using 4-5 concise bullet points."
            } else {
                "Summarize in simple, warm English using 4-5 concise bullet points."
            }
            val prompt = """
                $langDirective
                Topic: $topicTitle
                Reference content:
                $articleBody

                Provide an easy-to-remember educational takeaway summary without inventing extra statistics or references.
            """.trimIndent()
            val response = withTimeout(AI_TIMEOUT_MS) {
                educationModel.generateContent(prompt)
            }
            val text = response.text
            if (text.isNullOrBlank()) {
                Result.failure(Exception("Model returned an empty summary."))
            } else {
                Result.success(text)
            }
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Summary timed out after 60 seconds."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Education summary failed", e)
            Result.failure(Exception(e.localizedMessage ?: "Unable to generate AI summary."))
        }
    }

    private fun org.json.JSONArray.toStringList(): List<String> =
        (0 until length()).mapNotNull { optString(it).takeIf { s -> s.isNotBlank() } }
}

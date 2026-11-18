package com.example.data.local

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HealthMateRepository(private val dao: HealthMateDao) {

    val medications: Flow<List<MedicationEntity>> = dao.observeMedications()
    val medicationLogs: Flow<List<MedicationLogEntity>> = dao.observeMedicationLogs()
    val journalEntries: Flow<List<JournalEntryEntity>> = dao.observeJournalEntries()
    val appointments: Flow<List<AppointmentEntity>> = dao.observeAppointments()
    val savedReports: Flow<List<SavedReportSummaryEntity>> = dao.observeSavedReports()

    suspend fun saveMedication(med: MedicationEntity): Long = dao.insertMedication(med)

    suspend fun updateMedication(med: MedicationEntity) = dao.updateMedication(med)

    suspend fun deleteMedication(id: Long) {
        dao.deleteLogsForMedication(id)
        dao.deleteMedicationById(id)
    }

    suspend fun logMedicationAction(med: MedicationEntity, status: String) {
        val todayLabel = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        dao.insertMedicationLog(
            MedicationLogEntity(
                medicationId = med.id,
                medicationName = med.name,
                dosage = med.prescribedDosage,
                status = status,
                dateLabel = todayLabel
            )
        )
    }

    suspend fun saveJournalEntry(entry: JournalEntryEntity) = dao.insertJournalEntry(entry)

    suspend fun deleteJournalEntry(id: Long) = dao.deleteJournalEntryById(id)

    suspend fun saveAppointment(appointment: AppointmentEntity): Long = dao.insertAppointment(appointment)

    suspend fun updateAppointment(appointment: AppointmentEntity) = dao.updateAppointment(appointment)

    suspend fun deleteAppointment(id: Long) = dao.deleteAppointmentById(id)

    suspend fun saveReportSummary(report: SavedReportSummaryEntity) = dao.insertSavedReport(report)

    suspend fun deleteReportSummary(id: Long) = dao.deleteSavedReportById(id)

    suspend fun clearAllUserData() {
        dao.clearMedicationLogs()
        dao.clearMedications()
        dao.clearJournalEntries()
        dao.clearAppointments()
        dao.clearSavedReports()
    }

    suspend fun seedSampleDemoData() {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val cal = Calendar.getInstance()

        // Medications
        val med1Id = dao.insertMedication(
            MedicationEntity(
                name = "Vitamin D3 Supplement",
                prescribedDosage = "1000 IU (user-entered)",
                reminderTime = "09:00 AM",
                frequency = "Once daily",
                startDate = "01 Nov 2026",
                notes = "Take after breakfast with water"
            )
        )
        dao.insertMedication(
            MedicationEntity(
                name = "Omega-3 Fish Oil",
                prescribedDosage = "1000 mg (user-entered)",
                reminderTime = "08:30 PM",
                frequency = "Once daily",
                startDate = "05 Nov 2026",
                notes = "Take with evening meal"
            )
        )
        dao.insertMedicationLog(
            MedicationLogEntity(
                medicationId = med1Id,
                medicationName = "Vitamin D3 Supplement",
                dosage = "1000 IU (user-entered)",
                status = "TAKEN",
                dateLabel = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            )
        )

        // 5 days of journal entries so charts look vibrant and meaningful
        val sampleDays = listOf(
            Triple(7.5f, 8, 35) to Pair("Good", "118/78"),
            Triple(6.5f, 6, 20) to Pair("Okay", "122/80"),
            Triple(8.0f, 9, 45) to Pair("Energized", "116/76"),
            Triple(7.0f, 7, 30) to Pair("Calm", "120/79"),
            Triple(7.8f, 8, 40) to Pair("Good", "118/77")
        )
        sampleDays.forEachIndexed { index, pair ->
            val dayCal = cal.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_YEAR, -(4 - index))
            val label = dateFormat.format(dayCal.time)
            dao.insertJournalEntry(
                JournalEntryEntity(
                    dateLabel = label,
                    observations = "Sample demo wellness log for $label. Feeling steady hydration and rest.",
                    sleepHours = pair.first.first,
                    waterGlasses = pair.first.second,
                    activityMinutes = pair.first.third,
                    mood = pair.second.first,
                    symptoms = if (index == 1) "Mild afternoon fatigue" else "None",
                    bloodPressure = pair.second.second,
                    bloodGlucoseMgDl = 94 + index * 2,
                    createdAt = dayCal.timeInMillis
                )
            )
        }

        // Sample Appointment
        dao.insertAppointment(
            AppointmentEntity(
                doctorName = "Dr. Ayesha Khan",
                specialty = "General Physician",
                clinicOrHospital = "City Care Medical Center",
                dateTimeLabel = "24 Nov 2026, 11:00 AM",
                reason = "Annual preventive checkup & review of routine blood work",
                symptomHistoryNotes = "Occasional mild afternoon fatigue over the past week; sleep averaging 7.3 hours.",
                generatedQuestions = "1. Are my routine vitamin D and blood sugar trends within an optimal preventive range?\n2. What lifestyle adjustments would help reduce mild afternoon fatigue?\n3. When should I schedule my next preventive screening?"
            )
        )
    }
}

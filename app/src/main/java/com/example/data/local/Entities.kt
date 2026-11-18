package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val prescribedDosage: String,
    val reminderTime: String,
    val frequency: String,
    val startDate: String,
    val endDate: String = "",
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "medication_logs")
data class MedicationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicationId: Long,
    val medicationName: String,
    val dosage: String,
    val status: String, // "TAKEN" or "MISSED"
    val dateLabel: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateLabel: String,
    val observations: String,
    val sleepHours: Float,
    val waterGlasses: Int,
    val activityMinutes: Int,
    val mood: String,
    val symptoms: String = "",
    val bloodPressure: String = "", // e.g. "120/80"
    val bloodGlucoseMgDl: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val doctorName: String,
    val specialty: String,
    val clinicOrHospital: String,
    val dateTimeLabel: String,
    val reason: String,
    val symptomHistoryNotes: String = "",
    val generatedQuestions: String = "",
    val documentChecklist: String = "Previous lab reports|Current medication list|Government ID / Insurance card|Symptom timeline notes",
    val checkedDocuments: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_reports")
data class SavedReportSummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val summary: String,
    val flaggedHighlights: String,
    val questionsForDoctor: String,
    val createdAt: Long = System.currentTimeMillis()
)

package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthMateDao {

    // Medications
    @Query("SELECT * FROM medications ORDER BY createdAt DESC")
    fun observeMedications(): Flow<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity): Long

    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteMedicationById(id: Long)

    // Medication Logs
    @Query("SELECT * FROM medication_logs ORDER BY timestamp DESC")
    fun observeMedicationLogs(): Flow<List<MedicationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationLog(log: MedicationLogEntity)

    @Query("DELETE FROM medication_logs WHERE medicationId = :medId")
    suspend fun deleteLogsForMedication(medId: Long)

    // Health Journal
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun observeJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntryById(id: Long)

    // Appointments
    @Query("SELECT * FROM appointments ORDER BY createdAt DESC")
    fun observeAppointments(): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointmentById(id: Long)

    // Saved Report Summaries
    @Query("SELECT * FROM saved_reports ORDER BY createdAt DESC")
    fun observeSavedReports(): Flow<List<SavedReportSummaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedReport(report: SavedReportSummaryEntity)

    @Query("DELETE FROM saved_reports WHERE id = :id")
    suspend fun deleteSavedReportById(id: Long)

    // Clear all local data for user privacy control
    @Query("DELETE FROM medications")
    suspend fun clearMedications()

    @Query("DELETE FROM medication_logs")
    suspend fun clearMedicationLogs()

    @Query("DELETE FROM journal_entries")
    suspend fun clearJournalEntries()

    @Query("DELETE FROM appointments")
    suspend fun clearAppointments()

    @Query("DELETE FROM saved_reports")
    suspend fun clearSavedReports()
}

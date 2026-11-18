package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MedicationEntity::class,
        MedicationLogEntity::class,
        JournalEntryEntity::class,
        AppointmentEntity::class,
        SavedReportSummaryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HealthMateDatabase : RoomDatabase() {
    abstract fun healthMateDao(): HealthMateDao

    companion object {
        @Volatile
        private var INSTANCE: HealthMateDatabase? = null

        fun getInstance(context: Context): HealthMateDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HealthMateDatabase::class.java,
                    "healthmate_local.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

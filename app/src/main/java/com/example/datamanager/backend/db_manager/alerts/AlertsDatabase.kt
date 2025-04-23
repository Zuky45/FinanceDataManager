package com.example.datamanager.backend.db_manager.alerts


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PriceAlert::class], version = 2)
abstract class AlertsDatabase : RoomDatabase() {
    abstract fun priceAlertDao(): PriceAlertDao

    companion object {
        @Volatile
        private var INSTANCE: AlertsDatabase? = null

        fun getDatabase(context: Context): AlertsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlertsDatabase::class.java,
                    "alerts_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
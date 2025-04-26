package com.example.datamanager.backend.db_manager.alerts

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * A Room database for managing price alerts.
 *
 * This database contains a single table for `PriceAlert` entities and provides
 * a DAO (`PriceAlertDao`) for accessing and manipulating the data.
 *
 * @property priceAlertDao The Data Access Object for interacting with the `PriceAlert` table.
 */
@Database(entities = [PriceAlert::class], version = 2)
abstract class AlertsDatabase : RoomDatabase() {
    /**
     * Provides access to the `PriceAlertDao` for database operations.
     *
     * @return An instance of `PriceAlertDao`.
     */
    abstract fun priceAlertDao(): PriceAlertDao

    companion object {
        @Volatile
        private var INSTANCE: AlertsDatabase? = null

        /**
         * Retrieves the singleton instance of the `AlertsDatabase`.
         *
         * If the database instance does not already exist, it is created using
         * the Room database builder. The database is named "new_alerts_database".
         *
         * @param context The application context used to initialize the database.
         * @return The singleton instance of `AlertsDatabase`.
         */
        fun getDatabase(context: Context): AlertsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlertsDatabase::class.java,
                    "new_alerts_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
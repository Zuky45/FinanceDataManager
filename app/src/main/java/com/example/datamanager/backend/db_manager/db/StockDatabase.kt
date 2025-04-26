package com.example.datamanager.backend.db_manager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * A Room database for managing historical stock actions.
 *
 * This database contains a single table for `HistoricalStockAction` entities and provides
 * a DAO (`HistoricalStockActionDao`) for accessing and manipulating the data.
 *
 * @property historicalStockActionDao The Data Access Object for interacting with the `historical_stock_actions` table.
 */
@Database(entities = [HistoricalStockAction::class], version = 2)
abstract class StockDatabase : RoomDatabase() {
    /**
     * Provides access to the `HistoricalStockActionDao` for database operations.
     *
     * @return An instance of `HistoricalStockActionDao`.
     */
    abstract fun historicalStockActionDao(): HistoricalStockActionDao

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        /**
         * Retrieves the singleton instance of the `StockDatabase`.
         *
         * If the database instance does not already exist, it is created using
         * the Room database builder. The database is named "stock_database".
         *
         * @param context The application context used to initialize the database.
         * @return The singleton instance of `StockDatabase`.
         */
        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
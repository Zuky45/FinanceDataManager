package com.example.datamanager.backend.db_manager.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoricalStockActionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(action: HistoricalStockAction): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(actions: List<HistoricalStockAction>): List<Long>

    @Query("SELECT * FROM historical_stock_actions WHERE symbol = :symbol AND timestamp > :startTime ORDER BY timestamp ASC")
    suspend fun getHistoricalData(symbol: String, startTime: Long): List<HistoricalStockAction>

    @Query("DELETE FROM historical_stock_actions WHERE timestamp < :cutoffTime")
    suspend fun deleteOldData(cutoffTime: Long): Int

    @Query("DELETE FROM historical_stock_actions")
    suspend fun clearAll(): Int
}
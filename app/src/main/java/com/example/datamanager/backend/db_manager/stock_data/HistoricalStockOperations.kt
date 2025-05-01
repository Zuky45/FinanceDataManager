// Documentation partially generated
// Refactoring done with copilot
// @ author: Michal Poprac
package com.example.datamanager.backend.db_manager.stock_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for interacting with the `historical_stock_actions` table in the database.
 *
 * This interface provides methods for inserting, querying, and deleting historical stock action data.
 */
@Dao
interface HistoricalStockActionDao {

    /**
     * Inserts a single historical stock action into the database.
     *
     * @param action The `HistoricalStockAction` object to insert.
     * @return The row ID of the inserted action, or -1 if the insertion was ignored due to a conflict.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(action: HistoricalStockAction): Long

    /**
     * Inserts multiple historical stock actions into the database.
     *
     * @param actions A list of `HistoricalStockAction` objects to insert.
     * @return A list of row IDs for the inserted actions. If an insertion is ignored due to a conflict, the corresponding ID will be -1.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(actions: List<HistoricalStockAction>): List<Long>

    /**
     * Retrieves historical stock actions for a specific symbol within a given time range.
     *
     * @param symbol The stock symbol to filter by (e.g., "AAPL").
     * @param startTime The start time (in milliseconds since the epoch) to filter actions.
     * @return A list of `HistoricalStockAction` objects ordered by timestamp in ascending order.
     */
    @Query("SELECT * FROM historical_stock_actions WHERE symbol = :symbol AND timestamp > :startTime ORDER BY timestamp ASC")
    suspend fun getHistoricalData(symbol: String, startTime: Long): List<HistoricalStockAction>

    /**
     * Deletes historical stock actions that occurred before a specified cutoff time.
     *
     * @param cutoffTime The cutoff time (in milliseconds since the epoch). Actions with timestamps earlier than this will be deleted.
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM historical_stock_actions WHERE timestamp < :cutoffTime")
    suspend fun deleteOldData(cutoffTime: Long): Int

    /**
     * Deletes all historical stock actions from the database.
     *
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM historical_stock_actions")
    suspend fun clearAll(): Int
}
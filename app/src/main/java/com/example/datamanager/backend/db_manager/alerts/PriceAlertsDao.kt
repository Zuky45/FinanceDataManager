// Documentation partially generated
// Refactoring done with copilot
// @ author: Michal Poprac
package com.example.datamanager.backend.db_manager.alerts

import androidx.room.*

/**
 * Represents a price alert entity in the database.
 *
 * @property symbol The stock symbol for the alert (primary key).
 * @property userId The ID of the user who created the alert.
 * @property price The price at which the alert should be triggered.
 * @property isEnabled Indicates whether the alert is active (default is true).
 * @property createdAt The timestamp when the alert was created (default is the current system time).
 */
@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey
    val symbol: String,
    val userId: String,
    val price: Double,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Data Access Object (DAO) for interacting with the `price_alerts` table in the database.
 */
@Dao
interface PriceAlertDao {

    /**
     * Retrieves all price alerts from the database.
     *
     * @return A list of all `PriceAlert` objects.
     */
    @Query("SELECT * FROM price_alerts")
    suspend fun getAllAlerts(): List<PriceAlert>

    /**
     * Retrieves a specific price alert by its stock symbol.
     *
     * @param symbol The stock symbol of the alert to retrieve.
     * @return The `PriceAlert` object for the given symbol, or null if not found.
     */
    @Query("SELECT * FROM price_alerts WHERE symbol = :symbol")
    suspend fun getAlertForSymbol(symbol: String): PriceAlert?

    /**
     * Retrieves all price alerts for a specific user.
     *
     * @param userId The ID of the user whose alerts should be retrieved.
     * @return A list of `PriceAlert` objects for the specified user.
     */
    @Query("SELECT * FROM price_alerts WHERE userId = :userId")
    suspend fun getAlertsForUser(userId: String): List<PriceAlert>

    /**
     * Retrieves a specific price alert for a user by stock symbol.
     *
     * @param userId The ID of the user.
     * @param symbol The stock symbol of the alert to retrieve.
     * @return The `PriceAlert` object for the given user and symbol, or null if not found.
     */
    @Query("SELECT * FROM price_alerts WHERE userId = :userId AND symbol = :symbol")
    suspend fun getAlertForUserAndSymbol(userId: String, symbol: String): PriceAlert?

    /**
     * Retrieves all enabled price alerts for a specific user.
     *
     * @param userId The ID of the user whose enabled alerts should be retrieved.
     * @return A list of enabled `PriceAlert` objects for the specified user.
     */
    @Query("SELECT * FROM price_alerts WHERE userId = :userId AND isEnabled = 1")
    suspend fun getEnabledAlertsForUser(userId: String): List<PriceAlert>

    /**
     * Inserts a new price alert into the database.
     *
     * If a conflict occurs (e.g., an alert with the same primary key already exists),
     * the existing alert will be replaced.
     *
     * @param alert The `PriceAlert` object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlert)

    /**
     * Deletes a specific price alert from the database.
     *
     * @param alert The `PriceAlert` object to delete.
     */
    @Delete
    suspend fun deleteAlert(alert: PriceAlert)

    /**
     * Deletes a price alert by its stock symbol.
     *
     * @param symbol The stock symbol of the alert to delete.
     */
    @Query("DELETE FROM price_alerts WHERE symbol = :symbol")
    suspend fun deleteAlertBySymbol(symbol: String)

    /**
     * Deletes a specific price alert for a user by stock symbol.
     *
     * @param userId The ID of the user.
     * @param symbol The stock symbol of the alert to delete.
     */
    @Query("DELETE FROM price_alerts WHERE userId = :userId AND symbol = :symbol")
    suspend fun deleteAlertForUser(userId: String, symbol: String)
}
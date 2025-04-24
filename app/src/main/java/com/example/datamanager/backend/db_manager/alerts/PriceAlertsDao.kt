package com.example.datamanager.backend.db_manager.alerts

import androidx.room.*

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey
    val symbol: String,
    val userId: String,
    val price: Double,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts")
    suspend fun getAllAlerts(): List<PriceAlert>

    @Query("SELECT * FROM price_alerts WHERE symbol = :symbol")
    suspend fun getAlertForSymbol(symbol: String): PriceAlert?

    @Query ("SELECT * FROM price_alerts WHERE userId = :userId")
    suspend fun getAlertsForUser(userId: String): List<PriceAlert>

    @Query("SELECT * FROM price_alerts WHERE userId = :userId AND symbol = :symbol")
    suspend fun getAlertForUserAndSymbol(userId: String, symbol: String): PriceAlert?

    @Query("SELECT * FROM price_alerts WHERE userId = :userId AND isEnabled = 1")
    suspend fun getEnabledAlertsForUser(userId: String): List<PriceAlert>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlert)

    @Delete
    suspend fun deleteAlert(alert: PriceAlert)

    @Query("DELETE FROM price_alerts WHERE symbol = :symbol")
    suspend fun deleteAlertBySymbol(symbol: String)

    @Query("DELETE FROM price_alerts WHERE userId = :userId AND symbol = :symbol")
    suspend fun deleteAlertForUser(userId: String, symbol: String)


}
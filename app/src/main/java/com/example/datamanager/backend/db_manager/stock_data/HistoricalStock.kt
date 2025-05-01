// Documentation partially generated
// Refactoring done with copilot
// @ author: Michal Poprac
package com.example.datamanager.backend.db_manager.stock_data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a historical stock action entity in the database.
 *
 * This entity is used to store historical stock data, including the stock symbol,
 * price, and the timestamp of the action.
 *
 * @property id The unique identifier for the historical stock action (auto-generated).
 * @property symbol The stock symbol associated with the action (e.g., "AAPL" for Apple).
 * @property price The price of the stock at the time of the action.
 * @property timestamp The time of the action, stored in milliseconds since the epoch.
 */
@Entity(
    tableName = "historical_stock_actions",
)
data class HistoricalStockAction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Auto-generated unique ID
    val symbol: String, // Stock symbol
    val price: Double, // Stock price at the time of the action
    val timestamp: Long // Time of the action in milliseconds
)
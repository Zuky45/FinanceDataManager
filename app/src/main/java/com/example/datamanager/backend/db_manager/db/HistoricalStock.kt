package com.example.datamanager.backend.db_manager.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "historical_stock_actions",
)
data class HistoricalStockAction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val price: Double,
    val timestamp: Long // Store time in milliseconds
)
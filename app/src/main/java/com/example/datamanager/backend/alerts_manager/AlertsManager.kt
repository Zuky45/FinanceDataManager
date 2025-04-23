package com.example.datamanager.backend.alerts_manager

import android.content.Context
import com.example.datamanager.backend.db_manager.alerts.AlertsDatabase
import com.example.datamanager.backend.db_manager.alerts.PriceAlert
import com.example.datamanager.backend.db_manager.auth.DBManager

class AlertsManager(private val context: Context) {
    private val _alertsDatabase = AlertsDatabase.getDatabase(context)
    private val _priceAlertDao = _alertsDatabase.priceAlertDao()
    private val _authManager = DBManager.getInstance()

    private fun loadUserId(): String? {
        return _authManager.getCurrentUserId()
    }

    suspend fun writeToDb(symbol: String, price: Double) {
        val userId = loadUserId() ?: return

        _priceAlertDao.insertAlert(
            PriceAlert(
                userId = userId,
                symbol = symbol,
                price = price
            )
        )
    }
    suspend fun deleteAlert(symbol: String) {
        val userId = loadUserId() ?: return
        _priceAlertDao.deleteAlertForUser(userId, symbol)
    }

    suspend fun readFromDb(): List<PriceAlert> {
        val userId = loadUserId() ?: return emptyList()
        return _priceAlertDao.getAlertsForUser(userId)
    }

    suspend fun triggerAlerts(): List<String> {
        val alerts = readFromDb()
        val triggeredAlerts = AlertsCalculations.calculateAlerts(alerts)
        return triggeredAlerts
    }




}
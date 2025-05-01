// Documentation partially generated
// Refactoring done with copilot
// @ author: Michal Poprac
package com.example.datamanager.backend.alerts_manager

import android.content.Context
import com.example.datamanager.backend.db_manager.alerts.AlertsDatabase
import com.example.datamanager.backend.db_manager.alerts.PriceAlert
import com.example.datamanager.backend.db_manager.auth.DBManager

/**
 * Manages price alerts for the application, including database operations
 * and triggering alerts based on current stock prices.
 *
 * @param context The application context used to initialize database and authentication managers.
 */
class AlertsManager(private val context: Context) {
    // Instance of the alerts database
    private val _alertsDatabase = AlertsDatabase.getDatabase(context)
    // Data Access Object (DAO) for interacting with price alerts in the database
    private val _priceAlertDao = _alertsDatabase.priceAlertDao()
    // Authentication manager for retrieving the current user ID
    private val _authManager = DBManager.getInstance()

    /**
     * Loads the current user's ID from the authentication manager.
     *
     * @return The user ID as a String, or null if no user is logged in.
     */
    private fun loadUserId(): String? {
        return _authManager.getCurrentUserId()
    }

    /**
     * Writes a new price alert to the database for the current user.
     *
     * @param symbol The stock symbol for which the alert is being created.
     * @param price The price at which the alert should be triggered.
     */
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

    /**
     * Deletes a price alert for the specified stock symbol from the database.
     *
     * @param symbol The stock symbol for which the alert should be deleted.
     */
    suspend fun deleteAlert(symbol: String) {
        val userId = loadUserId() ?: return
        _priceAlertDao.deleteAlertForUser(userId, symbol)
    }

    /**
     * Reads all price alerts for the current user from the database.
     *
     * @return A list of `PriceAlert` objects representing the user's alerts.
     *         Returns an empty list if no user is logged in.
     */
    suspend fun readFromDb(): List<PriceAlert> {
        val userId = loadUserId() ?: return emptyList()
        return _priceAlertDao.getAlertsForUser(userId)
    }

    /**
     * Triggers alerts by checking which price alerts have been met based on current prices.
     *
     * @return A list of strings containing the symbols of the triggered alerts.
     */
    suspend fun triggerAlerts(): List<String> {
        val alerts = readFromDb()
        val triggeredAlerts = AlertsCalculations.calculateAlerts(alerts)
        return triggeredAlerts
    }
}
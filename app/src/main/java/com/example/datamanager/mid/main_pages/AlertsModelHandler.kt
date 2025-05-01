// Documentation partially generated
// Refactoring done with copilot
// Some functionality implemented with use of copilot
// @ author: Michal Poprac
package com.example.datamanager.mid.main_pages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.alerts_manager.AlertsManager
import com.example.datamanager.backend.api_manager.ApiManager
import com.example.datamanager.backend.db_manager.alerts.PriceAlert
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing alerts and stock price data.
 * Handles interactions between the UI and backend services, including database and API calls.
 *
 * @param context The application context used to initialize backend managers.
 */
class AlertsModelHandler(context: Context) : ViewModel() {
    private val _alertsManager = AlertsManager(context) // Manages alerts in the database.
    private val _apiManager = ApiManager() // Handles API calls for stock data.

    // StateFlow for current stock prices.
    private val _currentPrices = MutableStateFlow<Map<String, Double>>(emptyMap())
    val currentPrices: StateFlow<Map<String, Double>> = _currentPrices.asStateFlow()

    // StateFlow for user-defined alerts.
    private val _alerts = MutableStateFlow<List<PriceAlert>>(emptyList())
    val alerts: StateFlow<List<PriceAlert>> = _alerts.asStateFlow()

    // StateFlow for available stock actions.
    private val _availableActions = MutableStateFlow<List<String>>(emptyList())
    val availableActions: StateFlow<List<String>> = _availableActions.asStateFlow()

    // StateFlow for triggered alerts.
    private val _triggeredAlerts = MutableStateFlow<List<String>>(emptyList())
    val triggeredAlerts: StateFlow<List<String>> = _triggeredAlerts.asStateFlow()

    init {
        // Initialize by loading data and setting up periodic refresh.
        loadAlerts()
        loadCurrentPrices()
        loadAvailableActions()
        findTriggeredAlerts()
        refreshAlerts()
    }

    /**
     * Refreshes all data by reloading alerts, prices, available actions, and triggered alerts.
     */
    fun refresh() {
        loadAlerts()
        loadCurrentPrices()
        loadAvailableActions()
        findTriggeredAlerts()
    }

    /**
     * Adds a new alert for a specific stock symbol and price.
     *
     * @param symbol The stock symbol for the alert.
     * @param price The price at which the alert should trigger.
     */
    fun addAlert(symbol: String, price: Double) {
        viewModelScope.launch {
            _alertsManager.writeToDb(symbol, price)
        }
        refresh()
    }

    /**
     * Loads all alerts from the database into the `_alerts` StateFlow.
     */
    fun loadAlerts() {
        viewModelScope.launch {
            val alerts = _alertsManager.readFromDb()
            _alerts.value = alerts
        }
    }

    /**
     * Deletes an alert for a specific stock symbol.
     *
     * @param symbol The stock symbol for the alert to delete.
     */
    fun deleteAlert(symbol: String) {
        viewModelScope.launch {
            _alertsManager.deleteAlert(symbol)
            loadAlerts()
        }
        loadAlerts()
        loadCurrentPrices()
        loadAvailableActions()
        refresh()
    }

    /**
     * Loads the current stock prices from the API into the `_currentPrices` StateFlow.
     */
    fun loadCurrentPrices() {
        viewModelScope.launch {
            _currentPrices.value = _apiManager.getCurrentPrices()
        }
    }

    /**
     * Loads the available stock actions from the API into the `_availableActions` StateFlow.
     */
    fun loadAvailableActions() {
        viewModelScope.launch {
            _availableActions.value = _apiManager.getAvailableActions()
        }
    }

    /**
     * Finds and updates the list of triggered alerts in the `_triggeredAlerts` StateFlow.
     */
    fun findTriggeredAlerts() {
        viewModelScope.launch {
            val triggeredAlerts = _alertsManager.triggerAlerts()
            _triggeredAlerts.value = triggeredAlerts
        }
    }

    /**
     * Clears all alerts from the database.
     */
    fun clearAllAlerts() {
        viewModelScope.launch {
            for (symbol in _availableActions.value) {
                _alertsManager.deleteAlert(symbol)
            }
        }
    }

    /**
     * Periodically refreshes alerts, prices, available actions, and triggered alerts every 5 seconds.
     */
    fun refreshAlerts() {
        viewModelScope.launch {
            while (true) {
                loadAlerts()
                loadCurrentPrices()
                loadAvailableActions()
                findTriggeredAlerts()
                delay(5000) // Refresh every 5 seconds
            }
        }
    }
}









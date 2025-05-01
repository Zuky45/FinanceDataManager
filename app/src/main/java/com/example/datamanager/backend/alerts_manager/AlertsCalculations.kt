// Documentation partially generated
// Refactoring done with copilot
// @ author: Michal Poprac

package com.example.datamanager.backend.alerts_manager

import com.example.datamanager.backend.api_manager.ApiManager
import com.example.datamanager.backend.db_manager.alerts.PriceAlert

/**
 * A utility class for performing calculations related to price alerts.
 */
class AlertsCalculations {
    companion object {
        /**
         * Calculates which price alerts have been triggered based on the current prices.
         *
         * @param alerts A list of `PriceAlert` objects representing the user's price alerts.
         * @return A list of strings containing the symbols of the triggered alerts.
         *
         * This function fetches the current prices using the `ApiManager`, compares them
         * with the alert prices, and determines which alerts have been triggered.
         *
         * - An alert is considered triggered if the current price is greater than or equal
         *   to the alert price.
         * - If the current price for a symbol is unavailable, the alert is ignored.
         *
         * @throws Exception If there is an issue fetching the current prices.
         */
        suspend fun calculateAlerts(alerts: List<PriceAlert>): List<String> {
            // Fetch the current prices from the API
            val prices = ApiManager().getCurrentPrices()

            // List to store the symbols of triggered alerts
            val triggeredAlerts = mutableListOf<String>()

            // Iterate through each alert to check if it has been triggered
            for (alert in alerts) {
                val symbol = alert.symbol // The stock symbol for the alert
                val currentPrice = prices[symbol] // The current price of the stock
                val price = alert.price // The alert price set by the user

                // Check if the current price is available and if the alert is triggered
                if (currentPrice != null) {
                    if (currentPrice >= price) {
                        triggeredAlerts.add(symbol) // Add the symbol to the triggered alerts list
                    }
                }
            }

            // Return the list of triggered alerts
            return triggeredAlerts
        }
    }
}





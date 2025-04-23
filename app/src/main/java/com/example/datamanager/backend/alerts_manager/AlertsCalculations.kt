package com.example.datamanager.backend.alerts_manager

import com.example.datamanager.backend.api_manager.ApiManager
import com.example.datamanager.backend.db_manager.alerts.PriceAlert

class AlertsCalculations {
    companion object {
        suspend fun calculateAlerts(alerts: List<PriceAlert>): List<String> {
            val prices = ApiManager().getCurrentPrices()
            val triggeredAlerts = mutableListOf<String>()

            for (alert in alerts) {
                val symbol = alert.symbol
                val currentPrice = prices[symbol]
                val price = alert.price

                if (currentPrice != null) {
                    if (price >= currentPrice) {
                        triggeredAlerts.add(symbol)
                    }

                }


            }
            return triggeredAlerts
        }
    }
}





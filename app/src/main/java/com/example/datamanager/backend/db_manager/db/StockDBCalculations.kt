package com.example.datamanager.backend.db_manager.db

import android.util.Log

class StockDBCalculations {
    companion object {
        /**
         * Calculates the mean of historical stock data and computes the percentage change
         * from the current price.
         *
         * @param historicalData List of historical stock actions
         * @param currentPrice The current price to compare against the mean
         * @return The percentage change between current price and the historical mean
         */
        fun calculatePercentageChangeFromMean(
            historicalData: List<HistoricalStockAction>,
            currentPrice: Double
        ): Double {
            if (historicalData.isEmpty()) {
                Log.d("StockDBCalculations", "No historical data found")
                return 0.0
            }

            // Calculate mean price from historical data
            val meanPrice = historicalData.map { it.price }.average()

            // Calculate percentage change
            val percentageChange = ((currentPrice - meanPrice) / meanPrice) * 100

            Log.d("StockDBCalculations",
                "Current: $currentPrice, Mean: $meanPrice, Change: $percentageChange%")

            // Round to 2 decimal places
            return Math.round(percentageChange * 100) / 100.0
        }

        /**
         * Calculates percentage changes for multiple stocks at once
         *
         * @param historicalDataMap Map of symbol to list of historical data
         * @param currentPrices Map of symbol to current price
         * @return Map of symbol to percentage change from mean
         */
        fun calculateAllPercentageChanges(
            historicalDataMap: Map<String, List<HistoricalStockAction>>,
            currentPrices: Map<String, Double>
        ): Map<String, Double> {
            val result = mutableMapOf<String, Double>()

            for ((symbol, historicalData) in historicalDataMap) {
                val currentPrice = currentPrices[symbol] ?: continue
                result[symbol] = calculatePercentageChangeFromMean(historicalData, currentPrice)
            }

            return result
        }
    }
}
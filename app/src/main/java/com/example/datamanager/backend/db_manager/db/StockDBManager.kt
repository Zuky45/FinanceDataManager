package com.example.datamanager.backend.db_manager.db


import android.content.Context
import android.util.Log
import com.example.datamanager.backend.api_manager.ApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.last
import java.util.Date

class StockDBManager(private val context: Context) {

    private val database = StockDatabase.getDatabase(context)
    private val dao = database.historicalStockActionDao()
    private val apiManager = ApiManager()
    private val symbols = apiManager.getAvailableActions()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Start scheduled tasks
        startDataCollection()
        startDataCleanup()
    }

    /**
     * Starts hourly data collection
     */
    private fun startDataCollection() {
        scope.launch {
            while (true) {
                storeCurrentData()
                delay(60 * 60 * 1000) // 1 hour
            }
        }
    }

    /**
     * Starts scheduled data cleanup every 3 hours
     */
    private fun startDataCleanup() {
        scope.launch {
            while (true) {
                cleanupOldData()
                delay(3 * 60 * 60 * 1000) // 3 hours
            }
        }
    }

    /**
     * Fetches and stores the current stock data in the database
     */
    private suspend fun storeCurrentData() {
        dao.clearAll()
        Log.d("StockDBManager", "Storing current stock data")
        val timestamp = System.currentTimeMillis()
        val stockActions = mutableListOf<HistoricalStockAction>()

        for (symbol in symbols) {
            try {
                val data = apiManager.fetchData(symbol)
                if (data != null && data.rowsCount() > 0) {
                    // Iterate through all rows in the DataFrame
                    for (rowIndex in 0 until data.rowsCount()) {
                        // Extract price from each row
                        val price = data["Price"][rowIndex] as Double
                        // Calculate the timestamp for each entry
                        val entryTimestamp = timestamp - (data.rowsCount() - rowIndex) * 1000 // Assuming 1 second per row

                        stockActions.add(
                            HistoricalStockAction(
                                symbol = symbol,
                                price = price,
                                timestamp = entryTimestamp
                            )
                        )
                    }

                    Log.d("StockDBManager", "Prepared ${data.rowsCount()} records for $symbol")
                }
            } catch (e: Exception) {
                Log.e("StockDBManager", "Error fetching data for $symbol", e)
            }
        }

        if (stockActions.isNotEmpty()) {
            dao.insertAll(stockActions)
            Log.d("StockDBManager", "Stored total of ${stockActions.size} records at ${Date(timestamp)}")
        }
    }

    /**
     * Removes data older than 24 hours
     */
    private suspend fun cleanupOldData() {
        val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        val deletedCount = dao.deleteOldData(cutoffTime)
        Log.d("StockDBManager", "Cleaned up $deletedCount old entries")
    }

    /**
     * Manually trigger data storage
     */
    suspend fun forceDataUpdate() {
        storeCurrentData()
    }

    suspend fun calculatePriceChange(symbol: String): Double {
        val data = dao.getHistoricalData(symbol, System.currentTimeMillis() - (24 * 60 * 60 * 1000))
        val currentPrice = data.lastOrNull()?.price ?: return 0.0
        val change = StockDBCalculations.calculatePercentageChangeFromMean(data,currentPrice);
        return change;


    }
}
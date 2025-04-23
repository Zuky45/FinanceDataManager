package com.example.datamanager.backend.api_manager

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.lastOrNull
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.toDataFrame

/**
 * Data class representing the response from Yahoo Finance API.
 */
data class YahooFinanceResponse(val chart: Chart)

/**
 * Data class representing the chart data in the Yahoo Finance response.
 */
data class Chart(val result: List<Result>)

/**
 * Data class representing the result data in the chart.
 */
data class Result(val timestamp: List<Long>, val indicators: Indicators)

/**
 * Data class representing the indicators in the result.
 */
data class Indicators(val quote: List<Quote>)

/**
 * Data class representing the quote data in the indicators.
 */
data class Quote(val close: List<Double?>)

/**
 * Data class representing a stock entry with timestamp and close price.
 */
data class StockEntry(val timestamp: Long, val close: Double)

/**
 * Class responsible for managing API interactions.
 */
class ApiManager {

    /**
     * Returns a list of available stock actions.
     *
     * @return List of stock action symbols.
     */
    fun getAvailableActions(): List<String> {
        return listOf("AAPL", "GOOGL", "MSFT", "AMZN", "TSLA")
    }

    /**
     * Fetches data for a given stock action from Yahoo Finance API and returns a DataFrame.
     *
     * @param action The stock action symbol to fetch data for.
     * @return DataFrame containing the stock data or null if there was an error.
     */
    suspend fun fetchData(action: String): DataFrame<StockEntry>? = withContext(Dispatchers.IO) {
        try {
            val json = fetchJsonFromApi(action) ?: return@withContext null
            val financeData = parseJsonResponse(json) ?: return@withContext null
            val stockEntries = extractStockEntries(financeData)
            convertToDataFrame(stockEntries)
        } catch (e: Exception) {
            Log.e("ApiManager", "Error fetching data: ${e.message}", e)
            null
        }
    }

    /**
     * Fetches JSON data from Yahoo Finance API.
     *
     * @param action The stock action symbol to fetch data for.
     * @return JSON string response or null if request failed.
     */
    private fun fetchJsonFromApi(action: String): String? {
        val client = OkHttpClient()
        val url = "https://query1.finance.yahoo.com/v8/finance/chart/${action}?range=2h&interval=1m"
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            println("Error: ${response.code}")
            return null
        }

        val json = response.body?.string()
        if (json.isNullOrEmpty()) {
            println("Error: Empty response from API")
            return null
        }

        return json
    }

    /**
     * Parses JSON response into a YahooFinanceResponse object.
     *
     * @param json The JSON string to parse.
     * @return YahooFinanceResponse object or null if parsing failed.
     */
    private fun parseJsonResponse(json: String): YahooFinanceResponse? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(YahooFinanceResponse::class.java)
        val financeData = adapter.fromJson(json)

        if (financeData == null || financeData.chart.result.isEmpty()) {
            println("Error: Invalid data from API")
            return null
        }

        return financeData
    }

    /**
     * Extracts stock entries from the finance data.
     *
     * @param financeData The YahooFinanceResponse object containing the data.
     * @return List of StockEntry objects.
     */
    private fun extractStockEntries(financeData: YahooFinanceResponse): List<StockEntry> {
        val result = financeData.chart.result.first()
        val timestamps = result.timestamp
        val closePrices = result.indicators.quote.first().close

        return timestamps.zip(closePrices)
            .takeLast(100)
            .mapIndexedNotNull { _, (time, price) ->
                if (price != null) StockEntry(time, price) else null
            }
    }

    /**
     * Converts a list of stock entries to a DataFrame.
     *
     * @param stockEntries List of StockEntry objects.
     * @return DataFrame with normalized time indices and renamed columns.
     */
    private fun convertToDataFrame(stockEntries: List<StockEntry>): DataFrame<StockEntry> {
        return stockEntries.mapIndexed { index, entry -> entry.copy(timestamp = (index + 1).toLong()) }
            .toDataFrame()
            .rename("timestamp" to "Time", "close" to "Price")
    }

    suspend fun getCurrentPrices(): Map<String, Double> {
        val actions = getAvailableActions()
        val prices = mutableMapOf<String, Double>()

        for (action in actions) {
            val dataFrame = fetchData(action)
            if (dataFrame != null && dataFrame.rowsCount() > 0) {
                val latestPrice = dataFrame["Price"].lastOrNull() as? Double
                if (latestPrice != null) {
                    prices[action] = latestPrice
                }
            }
        }

        return prices
    }

}

/**
 * Main function to execute the API data fetch.
 */
suspend fun main() {
    val apiManager = ApiManager()
    val dataFrame = apiManager.fetchData("GOOGL")
    println(dataFrame)

}
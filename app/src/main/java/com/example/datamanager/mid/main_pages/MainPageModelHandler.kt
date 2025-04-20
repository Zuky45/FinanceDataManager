package com.example.datamanager.mid.main_pages

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.api_manager.ApiManager
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.backend.db_manager.db.StockDBManager
import com.example.datamanager.backend.db_manager.db.StockDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.last
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a stock action with its name, price, and percentage change.
 *
 * @property name The name or symbol of the stock.
 * @property price The current price of the stock.
 * @property change The percentage change in the stock price compared to the previous value.
 */
data class StockAction(
    val name: String,
    val price: Double,
    val change: Double
)

/**
 * ViewModel class responsible for managing the state and logic of the main page.
 * It fetches stock data, calculates price changes, and updates the UI state.
 */
class MainPageModelHandler(application: Application) : AndroidViewModel(application) {

    // StateFlow to indicate whether data is being loaded
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // StateFlow to hold the list of stock actions
    private val _actions = MutableStateFlow<List<StockAction>>(emptyList())
    val actions = _actions.asStateFlow()

    // StateFlow to hold detailed stock data for visualization
    private val _stockDataMap = MutableStateFlow<Map<String, DataFrame<StockEntry>>>(emptyMap())
    val stockDataMap: StateFlow<Map<String, DataFrame<StockEntry>>> = _stockDataMap.asStateFlow()

    // StateFlow to hold the last refresh time
    private val _refreshTime = MutableStateFlow("")
    val refreshTime = _refreshTime.asStateFlow()

    // List of available stock symbols
    private val symbols = ApiManager().getAvailableActions()


    // Instance of ApiManager to fetch stock data
    private val apiManager = ApiManager()

    private val dbManager = StockDBManager(application);

    /**
     * Initializes the ViewModel by loading stock actions when the ViewModel is created.
     */
    init {
        viewModelScope.launch {
            loadActions()
        }
    }

    /**
     * Loads stock actions by fetching data for each symbol, calculating price changes,
     * and updating the state with the latest stock actions and refresh time.
     * Also stores detailed stock data for the graph visualization.
     */
    private suspend fun loadActions() {
        _isLoading.value = true
        Log.d("MainPageModelHandler", "Fetching stock data for multiple symbols")

        val stockActions = mutableListOf<StockAction>()
        val newStockDataMap = mutableMapOf<String, DataFrame<StockEntry>>()

        for (symbol in symbols) {
            try {
                val data = apiManager.fetchData(symbol)
                if (data != null && data.rowsCount() > 0) {
                    // Store the complete data frame for graph visualization
                    newStockDataMap[symbol] = data

                    // Get the most recent stock entry
                    val latestEntry = data["Price"].last()
                    val price = latestEntry as Double
                    val change = dbManager.calculatePriceChange(symbol)

                    // Add the stock action to the list
                    stockActions.add(
                        StockAction(
                            name = symbol,
                            price = Math.round(price * 100) / 100.0,
                            change = Math.round(change * 100) / 100.0
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("MainPageModelHandler", "Error fetching data for $symbol", e)
            }
        }

        // Update the state with the fetched stock actions, data for graphs, and refresh time
        _actions.value = stockActions
        _stockDataMap.value = newStockDataMap
        _refreshTime.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _isLoading.value = false
    }


    /**
     * Refreshes the stock prices by reloading the stock actions.
     */
    fun refreshPrices() {
        viewModelScope.launch {
            loadActions()
        }
    }

}
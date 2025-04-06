package com.example.datamanager.mid

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.api_manager.StockEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * ViewModel class to handle stock data fetching and state management.
 * Extends ModelHandler to leverage common functionality.
 */
class StockModelHandler : ModelHandler() {
    // MutableStateFlow to hold the stock data
    private val _stockData = MutableStateFlow<DataFrame<StockEntry>?>(null)

    // Publicly exposed StateFlow for stock data
    val stockData: StateFlow<DataFrame<StockEntry>?> = _stockData.asStateFlow()

    /**
     * Fetches stock data for the given symbol.
     *
     * @param symbol The stock symbol to fetch data for.
     */
    fun loadStockData(symbol: String) {
        loadData {
            Log.d("StockViewModel", "Fetching data for $symbol")
            val result = getApiManager().fetchData(symbol)

            if (result != null) {
                Log.d("StockViewModel", "Data received: ${result.rowsCount()} rows")
                _stockData.value = result
            } else {
                getError().value = "No data available for $symbol"
            }
        }
    }
}
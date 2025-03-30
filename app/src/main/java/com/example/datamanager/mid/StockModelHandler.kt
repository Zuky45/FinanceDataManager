package com.example.datamanager.mid

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.api_manager.ApiManager
import com.example.datamanager.backend.api_manager.StockEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * ViewModel class to handle stock data fetching and state management.
 */
class StockModelHandler : ViewModel() {
    private val apiManager = ApiManager()

    // MutableStateFlow to hold the stock data
    private val _stockData = MutableStateFlow<DataFrame<StockEntry>?>(null)

    // Publicly exposed StateFlow for stock data
    val stockData: StateFlow<DataFrame<StockEntry>?> = _stockData.asStateFlow()

    // MutableStateFlow to indicate loading state
    private val _isLoading = MutableStateFlow(false)

    // Publicly exposed StateFlow for loading state
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // MutableStateFlow to hold error messages
    private val _error = MutableStateFlow<String?>(null)

    // Publicly exposed StateFlow for error messages
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Fetches stock data for the given symbol.
     *
     * @param symbol The stock symbol to fetch data for.
     */
    fun loadStockData(symbol: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d("StockViewModel", "Fetching data for $symbol")
                val result = apiManager.fetchData(symbol)

                if (result != null) {
                    Log.d("StockViewModel", "Data received: ${result.rowsCount()} rows")
                    _stockData.value = result
                } else {
                    _error.value = "No data available for $symbol"
                }
            } catch (e: Exception) {
                Log.e("StockViewModel", "Error fetching data", e)
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
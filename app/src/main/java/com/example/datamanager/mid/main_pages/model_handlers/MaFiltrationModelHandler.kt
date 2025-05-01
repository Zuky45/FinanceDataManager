// Documentation partially generated
// Refactoring done with copilot
// Some functionality implemented with use of copilot
// @ author: Michal Poprac
package com.example.datamanager.mid.main_pages.model_handlers

import android.util.Log
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.backend.models.MaFiltration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * ViewModel that handles Moving Average filtration operations on stock data.
 * This class retrieves stock data, applies MA filtration, and provides the filtered data
 * through state flows for UI consumption.
 */
class MaFiltrationModelHandler : ModelHandler() {
    // MutableStateFlow to hold the stock data for calculation
    private val _stockData = MutableStateFlow<DataFrame<StockEntry>?>(null)
    val stockData: StateFlow<DataFrame<StockEntry>?> = _stockData.asStateFlow()

    // MutableStateFlow to hold the MA filtration data
    private val _maFiltrationData = MutableStateFlow<DataFrame<*>?>(null)
    val maFiltrationData: StateFlow<DataFrame<*>?> = _maFiltrationData.asStateFlow()

    // Window size for the moving average calculation
    private val _windowSize = MutableStateFlow(3)
    val windowSize: StateFlow<Int> = _windowSize.asStateFlow()

    /**
     * Sets the window size for the moving average calculation.
     * @param size The size of the window (must be positive)
     */
    fun setWindowSize(size: Int) {
        if (size > 0) {
            _windowSize.value = size
            // Recalculate if we already have data
            _stockData.value?.let { calculateMaFiltration(it) }
        }
    }

    /**
     * Loads and calculates the MA filtration model for the specified stock symbol.
     * @param symbol The stock symbol to calculate moving average for
     */
    fun loadMaFiltration(symbol: String) {
        loadData {
            Log.d("MaFiltrationModelHandler", "Fetching data for $symbol")
            val result = getApiManager().fetchData(symbol)

            if (result != null) {
                Log.d("MaFiltrationModelHandler", "Data received: ${result.rowsCount()} rows")
                _stockData.value = result
                calculateMaFiltration(result)
            } else {
                getError().value = "No data available for $symbol"
            }
        }
    }

    /**
     * Calculates moving average filtration for the given stock data.
     *
     * @param stockData The DataFrame containing stock entries to filter
     */
    private fun calculateMaFiltration(stockData: DataFrame<StockEntry>) {
        try {
            val maFiltration = MaFiltration(stockData, _windowSize.value)
            maFiltration.calculateModel()

            _maFiltrationData.value = maFiltration.getModel()

            if (_maFiltrationData.value == null) {
                getError().value = "Failed to calculate moving average filtration"
            }
        } catch (e: Exception) {
            Log.e("MaFiltrationModelHandler", "Error calculating MA filtration", e)
            getError().value = "Error calculating MA filtration: ${e.message}"
        }
    }

    /**
     * Clears previously loaded data and errors.
     */
    fun clear() {
        _maFiltrationData.value = null
        getError().value = null
    }


    companion object {
        @Volatile
        private var instance: MaFiltrationModelHandler? = null

        fun getInstance(): MaFiltrationModelHandler {
            return instance ?: synchronized(this) {
                instance ?: MaFiltrationModelHandler().also { instance = it }
            }
        }
    }

}
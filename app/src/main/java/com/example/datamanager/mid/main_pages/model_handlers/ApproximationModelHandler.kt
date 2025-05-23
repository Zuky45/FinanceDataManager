// Documentation partially generated
// Refactoring done with copilot
// Some functionality implemented with use of copilot
// @ author: Michal Poprac
package com.example.datamanager.mid.main_pages.model_handlers

import android.util.Log
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.backend.models.Approximation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * ViewModel class to handle polynomial approximation calculations.
 * Extends ModelHandler to leverage common functionality.
 */
class ApproximationModelHandler : ModelHandler() {
    // MutableStateFlow to hold the stock data for calculation
    private val _stockData = MutableStateFlow<DataFrame<StockEntry>?>(null)
    val stockData: StateFlow<DataFrame<StockEntry>?> = _stockData.asStateFlow()

    // MutableStateFlow to hold the approximation data
    private val _approximationData = MutableStateFlow<DataFrame<*>?>(null)
    val approximationData: StateFlow<DataFrame<*>?> = _approximationData.asStateFlow()

    // MutableStateFlow for polynomial coefficients
    private val _coefficients = MutableStateFlow<DoubleArray?>(null)
    val coefficients: StateFlow<DoubleArray?> = _coefficients.asStateFlow()

    // Default polynomial degree
    private var _degree = MutableStateFlow<Int>(1)
    val degree: StateFlow<Int> = _degree.asStateFlow()

    private var _mse = MutableStateFlow<Double>(0.0)
    val mse: StateFlow<Double> = _mse.asStateFlow()

    /**
     * Sets the polynomial degree for the approximation.
     *
     * @param newDegree The degree of polynomial to use for approximation
     */
    fun setDegree(newDegree: Int) {
        _degree.value = newDegree
        // Recalculate if we already have data
        _stockData.value?.let { calculateApproximation(it) }
    }

    /**
     * Fetches stock data and calculates its polynomial approximation.
     *
     * @param symbol The stock symbol to fetch data for.
     */
    fun loadApproximation(symbol: String) {
        loadData {
            Log.d("ApproximationViewModel", "Fetching data for $symbol")
            val result = getApiManager().fetchData(symbol)

            if (result != null) {
                Log.d("ApproximationViewModel", "Data received: ${result.rowsCount()} rows")
                _stockData.value = result
                calculateApproximation(result)
            } else {
                getError().value = "No data available for $symbol"
            }
        }
    }
    /**
     * Calculates polynomial approximation for the given stock data.
     *
     * @param stockData The DataFrame containing stock entries to approximate
     */
    private fun calculateApproximation(stockData: DataFrame<StockEntry>) {
        try {
            val approximation = Approximation(stockData, _degree.value)
            approximation.calculateModel()

            _approximationData.value = approximation.getModel()
            _coefficients.value = approximation.getCoefficients()
            _mse.value = approximation.calculateMSE()

            if (_approximationData.value == null) {
                getError().value = "Failed to calculate approximation model"
            }
        } catch (e: Exception) {
            Log.e("ApproximationViewModel", "Error calculating approximation", e)
            getError().value = "Error calculating approximation: ${e.message}"
        }
    }


    companion object {
        @Volatile
        private var instance: ApproximationModelHandler? = null

        fun getInstance(): ApproximationModelHandler {
            return instance ?: synchronized(this) {
                instance ?: ApproximationModelHandler().also { instance = it }
            }
        }
    }
}
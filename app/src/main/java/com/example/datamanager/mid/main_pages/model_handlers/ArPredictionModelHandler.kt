package com.example.datamanager.mid.main_pages.model_handlers

import android.util.Log
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.backend.models.ArPrediction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * Handler class for managing the Auto-Regressive (AR) Prediction model.
 * This class provides functionality to load stock data, calculate AR predictions,
 * and manage related state flows.
 */
class ArPredictionModelHandler : ModelHandler() {
    // MutableStateFlow to hold the stock data for calculation
    private val _stockData = MutableStateFlow<DataFrame<StockEntry>?>(null)
    val stockData: StateFlow<DataFrame<StockEntry>?> = _stockData.asStateFlow()

    // MutableStateFlow to hold the AR prediction data
    private val _arPredictionData = MutableStateFlow<DataFrame<*>?>(null)
    val arPredictionData: StateFlow<DataFrame<*>?> = _arPredictionData.asStateFlow()

    // MutableStateFlow for AR coefficients
    private val _coefficients = MutableStateFlow<DoubleArray?>(null)
    val coefficients: StateFlow<DoubleArray?> = _coefficients.asStateFlow()

    // Default AR order
    private var _order = MutableStateFlow<Int>(5)
    val order: StateFlow<Int> = _order.asStateFlow()

    // Default prediction horizon
    private var _predictionHorizon = MutableStateFlow<Int>(10)
    val predictionHorizon: StateFlow<Int> = _predictionHorizon.asStateFlow()

    /**
     * Sets the AR order for the prediction.
     *
     * @param newOrder The order of the AR model to use for prediction.
     */
    fun setOrder(newOrder: Int) {
        if (newOrder > 0) {
            _order.value = newOrder
            // Recalculate if we already have data
            _stockData.value?.let { calculateArPrediction(it) }
        } else {
            getError().value = "AR order must be positive"
        }
    }

    /**
     * Sets the prediction horizon (number of data points to predict).
     *
     * @param horizon The number of data points to predict.
     */
    fun setPredictionHorizon(horizon: Int) {
        if (horizon > 0) {
            _predictionHorizon.value = horizon
            // Recalculate if we already have data
            _stockData.value?.let { calculateArPrediction(it) }
        } else {
            getError().value = "Prediction horizon must be positive"
        }
    }

    /**
     * Loads and calculates the AR prediction model for the specified stock symbol.
     *
     * @param symbol The stock symbol to calculate AR prediction for.
     */
    fun loadArPrediction(symbol: String) {
        loadData {
            Log.d("ArPredictionModelHandler", "Fetching data for $symbol")
            val result = getApiManager().fetchData(symbol)

            if (result != null) {
                Log.d("ArPredictionModelHandler", "Data received: ${result.rowsCount()} rows")
                _stockData.value = result
                calculateArPrediction(result)
            } else {
                getError().value = "No data available for $symbol"
            }
        }
    }

    /**
     * Calculates AR prediction for the given stock data.
     *
     * @param stockData The DataFrame containing stock entries for prediction.
     */
    private fun calculateArPrediction(stockData: DataFrame<StockEntry>) {
        try {
            val arPrediction = ArPrediction(stockData, _order.value, _predictionHorizon.value)
            arPrediction.calculateModel()

            _arPredictionData.value = arPrediction.getModel()
            _coefficients.value = arPrediction.getCoefficients()

            if (_arPredictionData.value == null) {
                getError().value = "Failed to calculate AR prediction model"
            }
        } catch (e: Exception) {
            Log.e("ArPredictionModelHandler", "Error calculating AR prediction", e)
            getError().value = "Error calculating AR prediction: ${e.message}"
        }
    }

    /**
     * Clears previously loaded data and errors.
     */
    fun clear() {
        _arPredictionData.value = null
        _stockData.value = null
        _coefficients.value = null
        getError().value = null
    }

    companion object {
        @Volatile
        private var instance: ArPredictionModelHandler? = null

        /**
         * Retrieves the singleton instance of the ArPredictionModelHandler.
         *
         * @return The singleton instance of ArPredictionModelHandler.
         */
        fun getInstance(): ArPredictionModelHandler {
            return instance ?: synchronized(this) {
                instance ?: ArPredictionModelHandler().also { instance = it }
            }
        }
    }
}
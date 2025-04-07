package com.example.datamanager.mid.main_pages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.api_manager.ApiManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.api.last
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StockAction(
    val name: String,
    val price: Double,
    val change: Double
)

class MainPageModelHandler : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _actions = MutableStateFlow<List<StockAction>>(emptyList())
    val actions = _actions.asStateFlow()

    private val _refreshTime = MutableStateFlow("")
    val refreshTime = _refreshTime.asStateFlow()

    private val symbols = ApiManager().getAvailableActions()
    private var lastPrices = mutableMapOf<String, Double>()

    private val apiManager = ApiManager()

    init {
        viewModelScope.launch {
            loadActions()
        }
    }

    suspend fun loadActions() {
        _isLoading.value = true
        Log.d("MainPageModelHandler", "Fetching stock data for multiple symbols")

        val stockActions = mutableListOf<StockAction>()

        for (symbol in symbols) {
            try {
                val data = apiManager.fetchData(symbol)
                if (data != null && data.rowsCount() > 0) {
                    // Get the most recent stock entry
                    val latestEntry = data["Price"].last()
                    val price = latestEntry as Double


                    val previousPrice = lastPrices[symbol] ?: 0.0

                    val change = if (previousPrice > 0) {
                        ((price - previousPrice) / previousPrice) * 100
                    } else 0.0


                    lastPrices[symbol] = price

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

        _actions.value = stockActions
        _refreshTime.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _isLoading.value = false
    }

    fun refreshPrices() {
        viewModelScope.launch {
            loadActions()
        }
    }
}
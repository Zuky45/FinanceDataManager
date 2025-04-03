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
 * Base ViewModel class for handling data models.
 * Provides common functionality for loading data and managing state.
 */
abstract class ModelHandler : ViewModel() {
    protected val apiManager = ApiManager()

    // MutableStateFlow to indicate loading state
    protected val _isLoading = MutableStateFlow(false)

    // Publicly exposed StateFlow for loading state
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // MutableStateFlow to hold error messages
    protected val _error = MutableStateFlow<String?>(null)

    // Publicly exposed StateFlow for error messages
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Executes a data loading operation with proper state management.
     *
     * @param action The suspend function to execute for data loading
     */
    protected fun loadData(action: suspend () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                action()
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, "Error loading data", e)
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Returns list of available stock symbols.
     *
     * @return List of available stock symbols
     */
    fun avaliableActions(): List<String> {
        return apiManager.getAvailableActions()
    }
}
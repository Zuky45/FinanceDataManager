package com.example.datamanager.mid.main_pages

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.alerts_manager.AlertsManager
import com.example.datamanager.backend.api_manager.ApiManager
import com.example.datamanager.backend.db_manager.alerts.PriceAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertsModelHandler(context: Context) : ViewModel() {
    private val alertsManager = AlertsManager(context)
    private val apiManager = ApiManager()
}



package com.example.datamanager.backend.models

import com.example.datamanager.backend.api_manager.ApiManager
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.maxOrNull
import org.jetbrains.kotlinx.dataframe.api.minOrNull
import kotlin.collections.average

abstract  class Model {
    private val name: String
    private val dataFrame: DataFrame<*>?

    constructor(name: String, dataFrame: DataFrame<*>? = null) {
        this.name = name
        this.dataFrame = dataFrame
    }
    abstract fun calculateModel()
    abstract fun getModel(): DataFrame<*>?
    fun getName(): String {
        return name
    }
    fun calculateAverage(): Double {
        return if (dataFrame != null && dataFrame.rowsCount() > 0 && "Price" in dataFrame.columnNames()) {
            val priceValues = dataFrame["Price"].map { it.toString().toDoubleOrNull() ?: 0.0 }
            priceValues.toList().average()
        } else {
            0.0
        }
    }

    fun getBiggestValue(columnName: String): Double {
        return if (dataFrame != null && dataFrame.rowsCount() > 0 && columnName in dataFrame.columnNames()) {
            val column = dataFrame[columnName].map { it.toString().toDoubleOrNull() ?: 0.0 }
            val maxValue = column.maxOrNull()
            maxValue ?: 0.0
        } else {
            0.0
        }
    }

    fun getLowestValue(columnName: String): Double {
        return if (dataFrame != null && dataFrame.rowsCount() > 0 && columnName in dataFrame.columnNames()) {
            val column = dataFrame[columnName].map { it.toString().toDoubleOrNull() ?: 0.0 }
            val minValue = column.minOrNull()
            minValue ?: 0.0
        } else {
            0.0
        }
    }










}


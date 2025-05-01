// Documentation partially generated
// Refactoring done with copilot
// @ author: Michal Poprac
package com.example.datamanager.backend.models

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.maxOrNull
import org.jetbrains.kotlinx.dataframe.api.minOrNull
import kotlin.collections.average

/**
 * Abstract class representing a data model.
 *
 * @property _name The name of the model.
 * @property _dataFrame The data frame associated with the model, default is null.
 */
abstract class Model(private val _name: String, private val _dataFrame: DataFrame<*>? = null) {

    /**
     * Abstract method to calculate the model. Must be implemented by subclasses.
     */
    abstract fun calculateModel()

    /**
     * Abstract method to retrieve the model's data frame. Must be implemented by subclasses.
     *
     * @return The data frame representing the model.
     */
    abstract fun getModel(): DataFrame<*>?

    /**
     * Retrieves the name of the model.
     *
     * @return The name of the model.
     */
    fun getName(): String {
        return _name
    }

    /**
     * Retrieves the data frame associated with the model.
     *
     * @return The data frame, or null if not set.
     */
    protected fun getDataFrame(): DataFrame<*>? {
        return _dataFrame
    }

    /**
     * Calculates the average value of the "Price" column in the data frame.
     *
     * @return The average value, or 0.0 if the column is not present or the data frame is empty.
     */
    fun calculateAverage(): Double {
        return if (_dataFrame != null && _dataFrame.rowsCount() > 0 && "Price" in _dataFrame.columnNames()) {
            val priceValues = _dataFrame["Price"].map { it.toString().toDoubleOrNull() ?: 0.0 }
            priceValues.toList().average()
        } else {
            0.0
        }
    }

    /**
     * Retrieves the largest value in the specified column.
     *
     * @param columnName The name of the column to search.
     * @return The largest value, or 0.0 if the column is not present or the data frame is empty.
     */
    fun getBiggestValue(columnName: String): Double {
        return if (_dataFrame != null && _dataFrame.rowsCount() > 0 && columnName in _dataFrame.columnNames()) {
            val column = _dataFrame[columnName].map { it.toString().toDoubleOrNull() ?: 0.0 }
            val maxValue = column.maxOrNull()
            maxValue ?: 0.0
        } else {
            0.0
        }
    }

    /**
     * Retrieves the smallest value in the specified column.
     *
     * @param columnName The name of the column to search.
     * @return The smallest value, or 0.0 if the column is not present or the data frame is empty.
     */
    fun getLowestValue(columnName: String): Double {
        return if (_dataFrame != null && _dataFrame.rowsCount() > 0 && columnName in _dataFrame.columnNames()) {
            val column = _dataFrame[columnName].map { it.toString().toDoubleOrNull() ?: 0.0 }
            val minValue = column.minOrNull()
            minValue ?: 0.0
        } else {
            0.0
        }
    }
}













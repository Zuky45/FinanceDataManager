package com.example.datamanager.backend.models

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import com.example.datamanager.backend.api_manager.StockEntry

/**
 * Moving Average Filtration model.
 *
 * This class implements a moving average filter that smooths time series data
 * by averaging values within a sliding window of configurable size.
 *
 * @param _data The DataFrame containing time series data to be filtered
 * @param _windowSize The size of the moving average window (default: 3)
 * @param _columnToFilter The name of the column to apply filtration to (default: "Price")
 */
class MaFiltration(
    private val _data: DataFrame<StockEntry>,
    private val _windowSize: Int = 3,
    private val _columnToFilter: String = "Price"
) : Model("MA Filtration") {

    private var _filteredData: DataFrame<*>? = null

    /**
     * Calculates the moving average filtration model.
     *
     * This method applies the moving average filter to the specified column
     * and stores the result in filteredData.
     */
    override fun calculateModel() {
        if (_data.rowsCount() < _windowSize) {
            throw IllegalArgumentException("Data size (${_data.rowsCount()}) must be greater than or equal to window size ($_windowSize)")
        }

        val originalValues = _data[_columnToFilter].toList().mapNotNull { it as? Double }
        val filteredValues = applyMovingAverage(originalValues, _windowSize)
        val timeColumn = _data["Time"].map { it }

        // Create a new DataFrame with the filtered values
        var timeList = timeColumn.toList();
        timeList = timeList.subList(_windowSize - 1, timeList.size)
        _filteredData = dataFrameOf("Time" to timeList, "MaFiltration" to filteredValues)
    }

    /**
     * Returns the calculated moving average filtration model as a DataFrame.
     *
     * @return DataFrame containing the original data and filtered values
     */
    override fun getModel(): DataFrame<*>? {
        return _filteredData
    }

    /**
     * Applies moving average filter to a list of Double values.
     *
     * @param data List of values to filter
     * @param windowSize Size of the moving average window
     * @return List of filtered values (size will be data.size - windowSize + 1)
     */
    private fun applyMovingAverage(data: List<Double>, windowSize: Int): List<Double> {
        val result = mutableListOf<Double>()

        for (i in 0..(data.size - windowSize)) {
            val window = data.subList(i, i + windowSize)
            val average = window.sum() / windowSize
            result.add(average)
        }

        return result
    }

}
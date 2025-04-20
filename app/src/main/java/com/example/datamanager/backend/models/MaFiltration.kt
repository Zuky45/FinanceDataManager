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
 * @param dataFrame The DataFrame containing time series data to be filtered
 * @param windowSize The size of the moving average window (default: 3)
 * @param columnToFilter The name of the column to apply filtration to (default: "Price")
 */
class MaFiltration(
    dataFrame: DataFrame<StockEntry>,
    private val windowSize: Int = 3,
    private val columnToFilter: String = "Price"
) : Model("MA Filtration", dataFrame) {

    private var filteredData: DataFrame<*>? = null

    /**
     * Calculates the moving average filtration model.
     *
     * This method applies the moving average filter to the specified column
     * and stores the result in filteredData.
     */
    override fun calculateModel() {
        val dataFrame = getDataFrame()
        if (dataFrame == null || dataFrame.rowsCount() < windowSize) {
            throw IllegalArgumentException("Data size (${dataFrame?.rowsCount() ?: 0}) must be greater than or equal to window size ($windowSize)")
        }

        val originalValues = dataFrame[columnToFilter].toList().mapNotNull { it as? Double } // Extract values from the specified column
        val filteredValues = applyMovingAverage(originalValues, windowSize) // Apply moving average filter
        val timeColumn = dataFrame["Time"].map { it } // Extract time values

        // Create a new DataFrame with the filtered values
        var timeList = timeColumn.toList() // Convert to list
        timeList = timeList.subList(windowSize - 1, timeList.size) // Adjust time list to match filtered values
        filteredData = dataFrameOf("Time" to timeList, "MaFiltration" to filteredValues) // Create new DataFrame
    }

    /**
     * Returns the calculated moving average filtration model as a DataFrame.
     *
     * @return DataFrame containing the original data and filtered values
     */
    override fun getModel(): DataFrame<*>? {
        return filteredData
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
            val window = data.subList(i, i + windowSize) // Extract the current window
            val average = window.sum() / windowSize // Calculate the average
            result.add(average) // Add the average to the result list
        }

        return result
    }
}
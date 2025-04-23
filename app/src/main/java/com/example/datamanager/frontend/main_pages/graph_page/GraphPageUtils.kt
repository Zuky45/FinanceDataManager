package com.example.datamanager.frontend.main_pages.graph_page

import android.graphics.Color as AndroidColor
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.mid.main_pages.model_handlers.ApproximationModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.ArPredictionModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.MaFiltrationModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.ModelHandler
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * Reloads the model data based on the selected model type and stock symbol.
 *
 * @param modelType The type of the model to reload (e.g., APPROXIMATION, MAFILTRATION).
 * @param symbol The stock symbol for which the model data should be reloaded.
 * @param modelHandler The handler responsible for managing the model logic.
 */
fun reloadModelData(
    modelType: ModelType,
    symbol: String,
    modelHandler: ModelHandler?
) {
    when (modelType) {
        // Reloads data for the Approximation model
        ModelType.APPROXIMATION -> (modelHandler as? ApproximationModelHandler)?.loadApproximation(symbol)
        // Reloads data for the Moving Average Filtration model
        ModelType.MAFILTRATION -> (modelHandler as? MaFiltrationModelHandler)?.loadMaFiltration(symbol)
        // Reloads data for the Auto-Regressive Prediction model
        ModelType.ARPREDICTION -> (modelHandler as? ArPredictionModelHandler)?.loadArPrediction(symbol)
        else -> {}
    }
}

/**
 * Updates the chart with stock and model data.
 *
 * @param chart The LineChart instance to update.
 * @param dataFrame The data frame containing stock data.
 * @param modelDataFrame The data frame containing model-specific data (optional).
 * @param modelName The name of the model being displayed.
 * @param modelColumnName The column name in the model data frame to display.
 */
fun updateChartData(
    chart: LineChart,
    dataFrame: DataFrame<StockEntry>,
    modelDataFrame: DataFrame<*>?,
    modelName: String,
    modelColumnName: String
) {
    val dataSets = ArrayList<ILineDataSet>()

    // Add stock data to the chart
    val stockEntries = ArrayList<Entry>()
    for (i in 0 until dataFrame.rowsCount()) {
        val price = dataFrame["Price"][i].toString().toFloatOrNull() ?: 0f
        stockEntries.add(Entry(i.toFloat(), price))
    }

    val stockDataSet = LineDataSet(stockEntries, "Price").apply {
        color = AndroidColor.rgb(66, 134, 244) // Set line color
        valueTextColor = AndroidColor.WHITE // Set text color
        lineWidth = 2f // Set line width
        setDrawCircles(false) // Disable circles on data points
        setDrawValues(false) // Disable value labels
        mode = LineDataSet.Mode.CUBIC_BEZIER // Use cubic bezier for smooth lines
        setDrawFilled(true) // Enable filled area under the line
        fillColor = AndroidColor.rgb(66, 134, 244) // Set fill color
        fillAlpha = 50 // Set fill transparency
    }

    dataSets.add(stockDataSet)

    // Add model data to the chart if available
    if (modelDataFrame != null && modelColumnName.isNotEmpty()) {
        try {
            if (hasColumn(modelDataFrame, modelColumnName)) {
                val modelEntries = ArrayList<Entry>()

                // Special handling for AR prediction - start from index 100
                val startIndex = if (modelName == "AR Prediction") dataFrame.rowsCount() else 0

                for (i in 0 until modelDataFrame.rowsCount()) {
                    val value = modelDataFrame[modelColumnName][i].toString().toFloatOrNull() ?: 0f

                    // Adjust x-value for AR prediction
                    val xValue = if (modelName == "AR Prediction") {
                        (i + startIndex).toFloat()
                    } else {
                        i.toFloat()
                    }

                    modelEntries.add(Entry(xValue, value))
                }

                val modelDataSet = LineDataSet(modelEntries, modelName).apply {
                    color = AndroidColor.rgb(255, 165, 0) // Set line color
                    valueTextColor = AndroidColor.WHITE // Set text color
                    lineWidth = 2f // Set line width
                    setDrawCircles(false) // Disable circles on data points
                    setDrawValues(false) // Disable value labels
                    mode = LineDataSet.Mode.HORIZONTAL_BEZIER // Use cubic bezier for smooth lines
                }
                dataSets.add(modelDataSet)
            }
        } catch (e: Exception) {
            android.util.Log.e("GraphPage", "Error adding model data", e)
        }
    }

    // Update the chart with the new data
    chart.data = LineData(dataSets)
    chart.invalidate() // Refresh the chart
}

/**
 * Checks if a column exists in the given data frame.
 *
 * @param dataFrame The data frame to check.
 * @param columnName The name of the column to look for.
 * @return True if the column exists, false otherwise.
 */
fun hasColumn(dataFrame: DataFrame<*>?, columnName: String): Boolean {
    if (dataFrame == null || columnName.isEmpty()) return false

    return try {
        dataFrame[columnName] // Attempt to access the column
        true // Column exists
    } catch (e: Exception) {
        false // Column does not exist
    }
}
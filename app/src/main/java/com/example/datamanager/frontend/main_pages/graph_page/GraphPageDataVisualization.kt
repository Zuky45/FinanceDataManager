/**
 * This file contains composable functions for data visualization in the graph page.
 * It provides components for displaying stock data in both chart and table formats,
 * supporting various analytical models.
 */
package com.example.datamanager.frontend.main_pages.graph_page

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.datamanager.R
import com.example.datamanager.backend.api_manager.StockEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.text.DecimalFormat

/**
 * Composable function to display the data view, which includes a chart and a data table.
 *
 * @param dataFrame The main data frame containing stock data.
 * @param modelDataFrame The data frame containing model-specific data (optional).
 * @param modelName The name of the model being displayed.
 * @param modelColumnName The column name in the model data frame to display.
 */
@Composable
fun DataView(
    dataFrame: DataFrame<StockEntry>,
    modelDataFrame: DataFrame<*>? = null,
    modelName: String = stringResource(R.string.data_view_approximation),
    modelColumnName: String = stringResource(R.string.data_view_approximation)
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Chart card
        StockChart(
            dataFrame = dataFrame,
            modelDataFrame = modelDataFrame,
            modelName = modelName,
            modelColumnName = modelColumnName,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 16.dp)
        )

        // Data table card
        DataTable(
            dataFrame = dataFrame,
            modelDataFrame = modelDataFrame,
            modelName = modelName,
            modelColumnName = modelColumnName,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

/**
 * Composable function to display a stock chart using the provided data.
 * Utilizes MPAndroidChart library (LineChart) to render the visualization.
 *
 * @param dataFrame The main data frame containing stock data.
 * @param modelDataFrame The data frame containing model-specific data (optional).
 * @param modelName The name of the model being displayed.
 * @param modelColumnName The column name in the model data frame to display.
 * @param modifier The modifier to be applied to the chart container.
 */
@Composable
fun StockChart(
    dataFrame: DataFrame<StockEntry>,
    modelDataFrame: DataFrame<*>?,
    modelName: String,
    modelColumnName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
    ) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    legend.textColor = AndroidColor.WHITE
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.textColor = AndroidColor.WHITE
                    axisLeft.textColor = AndroidColor.WHITE
                    axisRight.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    setDrawGridBackground(false)
                    setBackgroundColor(AndroidColor.TRANSPARENT)
                }
            },
            update = { chart ->
                updateChartData(
                    chart = chart,
                    dataFrame = dataFrame,
                    modelDataFrame = modelDataFrame,
                    modelName = modelName,
                    modelColumnName = modelColumnName
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Composable function to display a data table with stock and model data.
 * Shows time, price, and model values (if available) in a scrollable format.
 *
 * @param dataFrame The main data frame containing stock data.
 * @param modelDataFrame The data frame containing model-specific data (optional).
 * @param modelName The name of the model being displayed.
 * @param modelColumnName The column name in the model data frame to display.
 * @param modifier The modifier to be applied to the table container.
 */
@Composable
fun DataTable(
    dataFrame: DataFrame<StockEntry>,
    modelDataFrame: DataFrame<*>?,
    modelName: String,
    modelColumnName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Table header
            TableHeader(modelDataFrame, modelName, modelColumnName)

            Divider(color = DarkThemeColors.onSurface.copy(alpha = 0.2f))

            // Table content
            TableContent(dataFrame, modelDataFrame, modelColumnName)
        }
    }
}

/**
 * Composable function to display the header of the data table.
 * Shows column titles for time, price, and the model value (if available).
 *
 * @param modelDataFrame The data frame containing model-specific data (optional).
 * @param modelName The name of the model being displayed.
 * @param modelColumnName The column name in the model data frame to display.
 */
@Composable
fun TableHeader(
    modelDataFrame: DataFrame<*>?,
    modelName: String,
    modelColumnName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkThemeColors.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.data_view_time),
            fontWeight = FontWeight.Bold,
            color = DarkThemeColors.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.data_view_price),
            fontWeight = FontWeight.Bold,
            color = DarkThemeColors.onSurface,
            modifier = Modifier.weight(1f)
        )

        val hasModelColumn = hasColumn(modelDataFrame, modelColumnName)

        if (hasModelColumn) {
            Text(
                text = modelName,
                fontWeight = FontWeight.Bold,
                color = DarkThemeColors.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Composable function to display the content of the data table.
 * Renders rows of data with time, price, and model values (if available).
 *
 * @param dataFrame The main data frame containing stock data.
 * @param modelDataFrame The data frame containing model-specific data (optional).
 * @param modelColumnName The column name in the model data frame to display.
 */
@Composable
fun TableContent(
    dataFrame: DataFrame<StockEntry>,
    modelDataFrame: DataFrame<*>?,
    modelColumnName: String
) {
    val priceFormatter = remember { DecimalFormat("#,##0.00") }

    LazyColumn {
        items(dataFrame.rowsCount()) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
            ) {
                // Time column
                Text(
                    text = dataFrame["Time"][rowIndex].toString(),
                    color = DarkThemeColors.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Price column
                val priceValue = dataFrame["Price"][rowIndex].toString().toDoubleOrNull()
                Text(
                    text = if (priceValue != null) priceFormatter.format(priceValue) else "-",
                    color = DarkThemeColors.onSurface,
                    modifier = Modifier.weight(1f)
                )

                val hasModelColumn = hasColumn(modelDataFrame, modelColumnName)

                if (hasModelColumn) {
                    val modelIndex = if (rowIndex < modelDataFrame!!.rowsCount()) rowIndex else null
                    val modelValue = modelIndex?.let {
                        modelDataFrame[modelColumnName][it].toString().toDoubleOrNull()
                    }

                    Text(
                        text = if (modelValue != null) priceFormatter.format(modelValue) else "-",
                        color = DarkThemeColors.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (rowIndex < dataFrame.rowsCount() - 1) {
                Divider(color = DarkThemeColors.onSurface.copy(alpha = 0.1f))
            }
        }
    }
}
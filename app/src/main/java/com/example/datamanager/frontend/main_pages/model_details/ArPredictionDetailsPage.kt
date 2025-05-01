// Documentation partially generated
// Refactoring done with copilot
// Some functionality implemented with use of copilot
// @ author: Michal Poprac
/**
 * This file contains composable functions for the AR Prediction details page.
 * It displays detailed information about the autoregressive prediction model,
 * including coefficients, visualization chart, and prediction data table.
 */
package com.example.datamanager.frontend.main_pages.model_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.datamanager.R
import com.example.datamanager.frontend.main_pages.graph_page.DarkThemeColors
import com.example.datamanager.frontend.main_pages.graph_page.NoDataView
import com.example.datamanager.mid.main_pages.model_handlers.ArPredictionModelHandler
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.text.DecimalFormat
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Composable function to display the details page for the AR Prediction model.
 *
 * @param navController The NavController used for navigation.
 * @param arPredictionHandler The handler responsible for managing AR Prediction model data.
 */
@Composable
fun ArPredictionDetailsPage(
    navController: NavController,
    arPredictionHandler: ArPredictionModelHandler
) {
    // Collect states within the composable scope
    val stockData by arPredictionHandler.stockData.collectAsState()
    val predictionData by arPredictionHandler.arPredictionData.collectAsState()
    val coefficients by arPredictionHandler.coefficients.collectAsState()
    val order by arPredictionHandler.order.collectAsState()
    val horizon by arPredictionHandler.predictionHorizon.collectAsState()
    val formatter = remember { DecimalFormat("#,##0.00000") }

    Scaffold(
        topBar = {
            // Top app bar with a title and back navigation
            SmallTopAppBar(
                title = { Text(stringResource(R.string.ar_prediction_model_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = DarkThemeColors.surface,
                    titleContentColor = DarkThemeColors.onSurface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkThemeColors.background)
                .padding(16.dp)
        ) {
            // Display a "No Data" view if prediction data or coefficients are unavailable
            if (predictionData == null || coefficients == null) {
                NoDataView()
            } else {
                // Display the AR Prediction content
                ArPredictionContent(
                    order = order,
                    horizon = horizon,
                    coefficients = coefficients!!,
                    stockData = stockData,
                    predictionData = predictionData!!,
                    formatter = formatter
                )
            }
        }
    }
}

/**
 * Composable function to display the content of the AR Prediction details page.
 *
 * @param order The order of the AR model.
 * @param horizon The prediction horizon for the AR model.
 * @param coefficients The coefficients of the AR model.
 * @param stockData The data frame containing stock data (optional).
 * @param predictionData The data frame containing prediction data.
 * @param formatter The DecimalFormat instance for formatting coefficient values.
 */
@Composable
private fun ArPredictionContent(
    order: Int,
    horizon: Int,
    coefficients: DoubleArray,
    stockData: DataFrame<*>?,
    predictionData: DataFrame<*>,
    formatter: DecimalFormat
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // AR model information card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.ar_model_format, order),
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkThemeColors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.prediction_horizon_format, horizon),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.model_coefficients_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkThemeColors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display intercept
                Text(
                    text = stringResource(R.string.intercept_format, formatter.format(coefficients[0])),
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkThemeColors.onSurface
                )

                // Display AR coefficients
                for (i in 1 until coefficients.size) {
                    Text(
                        text = stringResource(R.string.ar_coefficient_format, i, formatter.format(coefficients[i])),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkThemeColors.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph card displaying the AR prediction chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        legend.textColor = AndroidColor.WHITE
                        xAxis.textColor = AndroidColor.WHITE
                        axisLeft.textColor = AndroidColor.WHITE
                        axisRight.isEnabled = false
                        setTouchEnabled(true)
                        setScaleEnabled(true)
                        setPinchZoom(true)
                        setBackgroundColor(AndroidColor.TRANSPARENT)
                    }
                },
                update = { chart ->
                    updateArPredictionChart(chart, predictionData)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data table displaying prediction data
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            Column {
                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.time_column),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = DarkThemeColors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.prediction_column),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = DarkThemeColors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Table data
                LazyColumn {
                    items(predictionData.rowsCount()) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = predictionData["Time"][index]?.toString() ?: "-",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = DarkThemeColors.onSurface
                            )
                            Text(
                                text = predictionData["Prediction"][index]?.toString() ?: "-",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = DarkThemeColors.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Updates the chart with AR prediction data.
 *
 * @param chart The LineChart instance to update.
 * @param dataFrame The data frame containing AR prediction data.
 */
private fun updateArPredictionChart(chart: LineChart, dataFrame: DataFrame<*>) {
    val entries = ArrayList<Entry>()
    val context = chart.context

    for (i in 0 until dataFrame.rowsCount()) {
        val x = dataFrame["Time"][i]?.toString()?.toFloatOrNull() ?: i.toFloat()
        val y = dataFrame["Prediction"][i]?.toString()?.toFloatOrNull() ?: 0f
        entries.add(Entry(x, y))
    }

    val dataSet = LineDataSet(entries, context.getString(R.string.ar_prediction_label)).apply {
        color = AndroidColor.rgb(0, 191, 255) // Deep sky blue
        lineWidth = 2.5f
        setDrawCircles(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawValues(false)
    }

    chart.data = LineData(dataSet)
    chart.invalidate()
}
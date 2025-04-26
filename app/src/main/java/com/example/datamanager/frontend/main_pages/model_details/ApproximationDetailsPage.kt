/**
 * This file contains composable functions for the polynomial approximation details page.
 * It displays detailed information about the approximation model, including coefficients,
 * equation visualization, and data table.
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
import androidx.compose.ui.geometry.RoundRect
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
import com.example.datamanager.mid.main_pages.model_handlers.ApproximationModelHandler
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.text.DecimalFormat
import java.util.Locale
import android.graphics.Color as AndroidColor

/**
 * Composable function to display the details page for polynomial approximation.
 *
 * @param navController The NavController used for navigation.
 * @param approximationHandler The handler responsible for managing the approximation model data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproximationDetailsPage(
    navController: NavController,
    approximationHandler: ApproximationModelHandler
) {
    // Collect states within the composable scope
    val dataFrame by approximationHandler.approximationData.collectAsState(initial = null)
    val degree by approximationHandler.degree.collectAsState(initial = 0)
    val coefficients by approximationHandler.coefficients.collectAsState(initial = null)
    val mse by approximationHandler.mse.collectAsState(initial = 0.0)

    Scaffold(
        topBar = {
            // Top app bar with a title and back navigation
            SmallTopAppBar(
                title = { Text(stringResource(R.string.polynomial_approximation_title)) },
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
            // Display a "No Data" view if data is unavailable, otherwise show the content
            if (dataFrame == null || coefficients == null) {
                NoDataView()
            } else {
                ApproximationContent(
                    degree = degree,
                    coefficients = coefficients!!,
                    dataFrame = dataFrame!!,
                    mseValue = mse,
                )
            }
        }
    }
}

/**
 * Composable function to display the content of the approximation details page.
 *
 * @param degree The degree of the polynomial approximation.
 * @param coefficients The coefficients of the polynomial equation.
 * @param dataFrame The data frame containing the approximation data.
 */
@Composable
private fun ApproximationContent(
    degree: Int,
    coefficients: DoubleArray,
    dataFrame: DataFrame<*>,
    mseValue : Double
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Polynomial model card displaying degree, equation, and coefficients, mean squared error
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.polynomial_degree_format, degree),
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkThemeColors.onSurface,

                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.mse_label, toNDecimalPlaces(mseValue,5)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.model_equation_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.onSurface
                )

                Text(
                    text = formatPolynomialEquation(coefficients),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkThemeColors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.coefficients_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.onSurface
                )

                coefficients.forEachIndexed { index, value ->
                    Text(
                        text = stringResource(R.string.coefficient_format, index, toNDecimalPlaces(value,5)),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkThemeColors.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph card displaying the approximation chart
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
                    updateApproximationChart(chart, dataFrame)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data table displaying the approximation data
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
                        text = stringResource(R.string.approximation_column),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = DarkThemeColors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Table data
                LazyColumn {
                    items(dataFrame.rowsCount()) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = dataFrame["Time"][index]?.toString() ?: "-",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = DarkThemeColors.onSurface
                            )
                            Text(
                                text = dataFrame["Approximation"][index]?.toString() ?: "-",
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
 * Updates the chart with approximation data only.
 *
 * @param chart The LineChart instance to update.
 * @param dataFrame The data frame containing the approximation data.
 */
private fun updateApproximationChart(chart: LineChart, dataFrame: DataFrame<*>) {
    val entries = ArrayList<Entry>()
    val context = chart.context

    for (i in 0 until dataFrame.rowsCount()) {
        val x = dataFrame["Time"][i]?.toString()?.toFloatOrNull() ?: i.toFloat()
        val y = dataFrame["Approximation"][i]?.toString()?.toFloatOrNull() ?: 0f
        entries.add(Entry(x, y))
    }

    val dataSet = LineDataSet(entries, context.getString(R.string.approximation_label)).apply {
        color = AndroidColor.rgb(255, 165, 0)
        lineWidth = 2.5f
        setDrawCircles(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawValues(false)
    }

    chart.data = LineData(dataSet)
    chart.invalidate()
}

/**
 * Formats a polynomial equation from the given coefficients.
 *
 * @param coefficients The array of coefficients for the polynomial equation.
 * @return A string representation of the polynomial equation.
 */
private fun formatPolynomialEquation(coefficients: DoubleArray): String {
    val terms = coefficients.mapIndexed { index, coef ->
        val formattedCoef = toNDecimalPlaces(coef,10)
        when (index) {
            0 -> formattedCoef
            1 -> "${formattedCoef}x"
            else -> "${formattedCoef}x^$index"
        }
    }
    return terms.reversed().joinToString(" + ")
}

private fun toNDecimalPlaces(value: Double, places : Int): Double {
    return String.format(Locale.US, "%.${places}f", value).toDouble()
}

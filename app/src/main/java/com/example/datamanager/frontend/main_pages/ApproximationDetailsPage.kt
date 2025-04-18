package com.example.datamanager.frontend.main_pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.datamanager.frontend.main_pages.graph_page.DarkThemeColors
import com.example.datamanager.frontend.main_pages.graph_page.NoDataView
import com.example.datamanager.mid.main_pages.ApproximationModelHandler
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.jetbrains.kotlinx.dataframe.DataFrame
import android.graphics.Color as AndroidColor

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

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Polynomial Approximation") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            if (dataFrame == null || coefficients == null) {
                NoDataView()
            } else {
                ApproximationContent(
                    degree = degree,
                    coefficients = coefficients!!,
                    dataFrame = dataFrame!!
                )
            }
        }
    }
}


@Composable
private fun ApproximationContent(
    degree: Int,
    coefficients: DoubleArray,
    dataFrame: DataFrame<*>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Polynomial model card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Polynomial Degree: $degree",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkThemeColors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Model Equation:",
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
                    text = "Coefficients:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.onSurface
                )

                coefficients.forEachIndexed { index, value ->
                    Text(
                        text = "a$index = ${String.format("%.6f", value)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkThemeColors.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph card
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

        // Data table
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
                        text = "Time",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = DarkThemeColors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Approximation",
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
 * Updates the chart with approximation data only
 */
private fun updateApproximationChart(chart: LineChart, dataFrame: DataFrame<*>) {
    val entries = ArrayList<Entry>()

    for (i in 0 until dataFrame.rowsCount()) {
        val x = dataFrame["Time"][i]?.toString()?.toFloatOrNull() ?: i.toFloat()
        val y = dataFrame["Approximation"][i]?.toString()?.toFloatOrNull() ?: 0f
        entries.add(Entry(x, y))
    }

    val dataSet = LineDataSet(entries, "Approximation").apply {
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
 * Formats polynomial equation from coefficients
 */
private fun formatPolynomialEquation(coefficients: DoubleArray): String {
    val terms = coefficients.mapIndexed { index, coef ->
        val formattedCoef = String.format("%.4f", coef)
        when (index) {
            0 -> formattedCoef
            1 -> "${formattedCoef}x"
            else -> "${formattedCoef}x^$index"
        }
    }
    return terms.reversed().joinToString(" + ")
}

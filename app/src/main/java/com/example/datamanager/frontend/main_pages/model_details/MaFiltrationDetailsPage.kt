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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.datamanager.frontend.main_pages.graph_page.DarkThemeColors
import com.example.datamanager.frontend.main_pages.graph_page.NoDataView
import com.example.datamanager.mid.main_pages.model_handlers.MaFiltrationModelHandler
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.jetbrains.kotlinx.dataframe.DataFrame
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Composable function to display the details page for the Moving Average (MA) Filtration model.
 *
 * @param navController The NavController used for navigation.
 * @param maFiltrationModelHandler The handler responsible for managing MA Filtration model data.
 */
@Composable
fun MaFiltrationDetailsPage(
    navController: NavController,
    maFiltrationModelHandler: MaFiltrationModelHandler
) {
    // Collect states within the composable scope
    val dataFrame by maFiltrationModelHandler.maFiltrationData.collectAsState(initial = null)
    val windowSize by maFiltrationModelHandler.windowSize.collectAsState(initial = 0)

    Scaffold(
        topBar = {
            // Top app bar with a title and back navigation
            SmallTopAppBar(
                title = { Text("MA Filtration") },
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
            // Display a "No Data" view if data is unavailable, otherwise show the content
            if (dataFrame == null) {
                NoDataView()
            } else {
                MaFiltrationContent(
                    windowSize = windowSize,
                    dataFrame = dataFrame!!
                )
            }
        }
    }
}

/**
 * Composable function to display the content of the MA Filtration details page.
 *
 * @param windowSize The size of the moving average window.
 * @param dataFrame The data frame containing the MA Filtration data.
 */
@Composable
private fun MaFiltrationContent(
    windowSize: Int,
    dataFrame: DataFrame<*>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Card displaying the window size
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Window size: $windowSize",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkThemeColors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph card displaying the MA Filtration chart
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
                    updateFiltrationChart(chart, dataFrame)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data table displaying the MA Filtration data
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
                        text = "MA Filtration",
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
                                text = dataFrame["MaFiltration"][index]?.toString() ?: "-",
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
 * Updates the chart with MA Filtration data.
 *
 * @param chart The LineChart instance to update.
 * @param dataFrame The data frame containing the MA Filtration data.
 */
private fun updateFiltrationChart(chart: LineChart, dataFrame: DataFrame<*>) {
    val entries = ArrayList<Entry>()

    for (i in 0 until dataFrame.rowsCount()) {
        val x = dataFrame["Time"][i]?.toString()?.toFloatOrNull() ?: i.toFloat()
        val y = dataFrame["MaFiltration"][i]?.toString()?.toFloatOrNull() ?: 0f
        entries.add(Entry(x, y))
    }

    val dataSet = LineDataSet(entries, "MA Filtration").apply {
        color = AndroidColor.rgb(255, 165, 0) // Orange color for the line
        lineWidth = 2.5f // Set line width
        setDrawCircles(false) // Disable circles on data points
        mode = LineDataSet.Mode.CUBIC_BEZIER // Use cubic bezier for smooth lines
        setDrawValues(false) // Disable value labels
    }

    chart.data = LineData(dataSet)
    chart.invalidate() // Refresh the chart
}

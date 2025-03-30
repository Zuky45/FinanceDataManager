package com.example.datamanager.main_pages

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.mid.StockModelHandler
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.text.DecimalFormat

// Dark theme color palette
object DarkThemeColors {
    val background = Color(0xFF121212)
    val surface = Color(0xFF1E1E1E)
    val surfaceVariant = Color(0xFF2D2D2D)
    val primary = Color(0xFF2196F3)
    val secondary = Color(0xFF03DAC6)
    val onBackground = Color.White
    val onSurface = Color.White
    val error = Color(0xFFCF6679)
}


@Composable
fun GraphPage(
    navController: NavController,
    stockSymbol: String = "AAPL",
    viewModel: StockModelHandler = viewModel()
) {
    // Collect state from ViewModel
    val stockData by viewModel.stockData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load data when the composable is first displayed
    LaunchedEffect(stockSymbol) {
        viewModel.loadStockData(stockSymbol)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkThemeColors.background)
            .padding(bottom = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "$stockSymbol Stock Data",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkThemeColors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { navController.navigate("main") },
            colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.primary),
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .align(Alignment.Start)
        ) {
            Text("← Back", color = Color.White)
        }

        when {
            isLoading -> {
                LoadingView(stockSymbol)
            }
            error != null -> {
                ErrorView(error!!) {
                    viewModel.loadStockData(stockSymbol)
                }
            }
            stockData != null -> {
                DataView(stockData!!)
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No data available",
                        color = DarkThemeColors.error
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView(stockSymbol: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = DarkThemeColors.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading $stockSymbol data...",
                color = DarkThemeColors.onBackground
            )
        }
    }
}

@Composable
private fun ErrorView(errorMessage: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = errorMessage,
                color = DarkThemeColors.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.primary)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun DataView(dataFrame: DataFrame<StockEntry>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Stock chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            AndroidView(
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        setTouchEnabled(true)
                        isDragEnabled = true
                        setScaleEnabled(true)
                        setPinchZoom(true)

                        // Dark theme styling
                        setBackgroundColor(AndroidColor.TRANSPARENT)
                        setGridBackgroundColor(AndroidColor.TRANSPARENT)
                        legend.textColor = AndroidColor.WHITE

                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            textColor = AndroidColor.WHITE
                            setDrawGridLines(true)
                            gridColor = AndroidColor.DKGRAY
                        }

                        axisLeft.apply {
                            textColor = AndroidColor.WHITE
                            setDrawGridLines(true)
                            gridColor = AndroidColor.DKGRAY
                        }

                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->
                    val entries = ArrayList<Entry>()

                    // Extract price data
                    for (i in 0 until dataFrame.rowsCount()) {
                        val price = dataFrame["Price"][i].toString().toFloatOrNull() ?: 0f
                        entries.add(Entry(i.toFloat(), price))
                    }

                    val dataSet = LineDataSet(entries, "Price").apply {
                        color = AndroidColor.rgb(66, 134, 244)
                        valueTextColor = AndroidColor.WHITE
                        lineWidth = 2f
                        setDrawCircles(false)
                        setDrawValues(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        setDrawFilled(true)
                        fillColor = AndroidColor.rgb(66, 134, 244)
                        fillAlpha = 50
                    }

                    chart.data = LineData(dataSet)
                    chart.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Data table
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkThemeColors.surfaceVariant)
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = "Time",
                        fontWeight = FontWeight.Bold,
                        color = DarkThemeColors.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Price",
                        fontWeight = FontWeight.Bold,
                        color = DarkThemeColors.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(color = DarkThemeColors.onSurface.copy(alpha = 0.2f))

                // Table content
                val priceFormatter = remember { DecimalFormat("#,##0.00") }

                LazyColumn {
                    items((0 until dataFrame.rowsCount()).toList()) { rowIndex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = dataFrame["Time"][rowIndex].toString(),
                                color = DarkThemeColors.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            val priceValue = dataFrame["Price"][rowIndex].toString().toDoubleOrNull()
                            Text(
                                text = if (priceValue != null) priceFormatter.format(priceValue) else "-",
                                color = DarkThemeColors.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowIndex < dataFrame.rowsCount() - 1) {
                            Divider(color = DarkThemeColors.onSurface.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}

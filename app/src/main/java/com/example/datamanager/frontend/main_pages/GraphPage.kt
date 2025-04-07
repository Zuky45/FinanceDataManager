package com.example.datamanager.frontend.main_pages

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.mid.ApproximationModelHandler
import com.example.datamanager.mid.StockModelHandler
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.text.DecimalFormat

/**
 * Object containing color definitions for the dark theme used throughout the application.
 * These colors follow Material Design principles and are used consistently across
 * all UI components to maintain a cohesive visual appearance.
 */
object DarkThemeColors {
    /** Main background color for screens */
    val background = Color(0xFF121212)

    /** Surface color for cards and elevated surfaces */
    val surface = Color(0xFF1E1E1E)

    /** Variant surface color for headers and differentiation */
    val surfaceVariant = Color(0xFF2D2D2D)

    /** Primary brand color for buttons and important UI elements */
    val primary = Color(0xFF2196F3)

    /** Secondary color for less prominent UI elements */
    val secondary = Color(0xFF03DAC6)

    /** Text and icon color on background */
    val onBackground = Color.White

    /** Text and icon color on surfaces */
    val onSurface = Color.White

    /** Error and warning color */
    val error = Color(0xFFCF6679)
}

/**
 * Enum class representing different types of data visualization models.
 * Used to switch between different representations of stock data.
 *
 * @property displayName Human-readable name of the model type displayed in the UI.
 */
enum class ModelType(val displayName: String) {
    /** Raw stock data without any modeling applied */
    NONE("Raw Data"),

    /** Polynomial approximation model of the stock data */
    APPROXIMATION("Approximation"),
}

/**
 * Composable function that displays the stock graph page with interactive features.
 * This page shows stock price data in graphical and tabular format, and allows
 * users to switch between different stocks and apply mathematical models to the data.
 *
 * The page layout includes:
 * - Stock selector dropdown
 * - Navigation controls
 * - Model type selection
 * - Interactive graph visualization
 * - Tabular data display
 *
 * @param navController Navigation controller for navigating between screens.
 * @param stockSymbol The default stock symbol to display. Defaults to "AAPL" (Apple Inc.).
 * @param stockViewModel ViewModel that handles stock data fetching and processing.
 * @param approximationViewModel ViewModel that handles mathematical approximation calculations.
 */
@Composable
fun GraphPage(
    // Navigation controller for navigating between screens
    navController: NavController,
    // Default stock symbol to display
    stockSymbol: String = "AAPL",
    // ViewModel that handles stock data fetching and processing
    stockViewModel: StockModelHandler = viewModel(),
    // ViewModel that handles mathematical approximation calculations
    approximationViewModel: ApproximationModelHandler = viewModel()
) {
    // RAW DATA

    // State variables to hold stock data, loading state, and error messages
    val stockData by stockViewModel.stockData.collectAsState()
    // Loading state and error message for stock data
    val isLoading by stockViewModel.isLoading.collectAsState()
    // Error message for stock data
    val error by stockViewModel.error.collectAsState()

    // Approximation data


    // Loading state and error message for approximation data
    val approximationData by approximationViewModel.approximationData.collectAsState()
    // Error message for approximation data
    val approxIsLoading by approximationViewModel.isLoading.collectAsState()
    // Error message for approximation data
    val approxError by approximationViewModel.error.collectAsState()


    // List of available stocks for selection
    val availableStocks = stockViewModel.avaliableActions()
    // State variables for managing dropdown menus and selected options
    var expandedStockMenu by remember { mutableStateOf(false) }
    // Selected stock symbol and model type
    var selectedStock by remember { mutableStateOf(stockSymbol) }
    // Selected model type for data visualization
    var selectedModel by remember { mutableStateOf(ModelType.NONE) }


    // Load stock data when the selected stock changes
    LaunchedEffect(selectedStock) {
        stockViewModel.loadStockData(selectedStock)
        if (selectedModel == ModelType.APPROXIMATION) {
            approximationViewModel.loadApproximation(selectedStock)
        }
    }

    // Main layout of the GraphPage
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkThemeColors.background)
            .padding(bottom = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Stock selection dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box {
                Button(
                    onClick = { expandedStockMenu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.primary)
                ) {
                    Text("Stock: $selectedStock")
                }

                DropdownMenu(
                    expanded = expandedStockMenu,
                    onDismissRequest = { expandedStockMenu = false }
                ) {
                    availableStocks.forEach { stock ->
                        DropdownMenuItem(
                            text = { Text(stock) },
                            onClick = {
                                selectedStock = stock
                                expandedStockMenu = false
                            }
                        )
                    }
                }
            }
        }
        // Spacer for spacing
        Spacer(modifier = Modifier.height(8.dp))
        // Header text for the graph page
        Text(
            text = "Stock Data for $selectedStock",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkThemeColors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("main") },
                colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.primary)
            ) {
                Text("Back", color = Color.White)
            }

            // Model type selection dropdown
            if (selectedModel == ModelType.APPROXIMATION) {
                val degreeOptions = listOf(1, 2, 3, 4, 5 ,6 ,7)
                var expandedDegree by remember { mutableStateOf(false) }
                var selectedDegree by remember { mutableStateOf(1) }
                // Degree selection dropdown
                Box {
                    OutlinedButton(
                        onClick = { expandedDegree = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DarkThemeColors.primary
                        )
                    ) {
                        Text("Degree: $selectedDegree")
                    }
                    // Dropdown menu for degree selection
                    DropdownMenu(
                        expanded = expandedDegree,
                        onDismissRequest = { expandedDegree = false }
                    ) {
                        degreeOptions.forEach { degree ->
                            DropdownMenuItem(
                                text = { Text(degree.toString()) },
                                onClick = {
                                    selectedDegree = degree
                                    expandedDegree = false
                                    approximationViewModel.setDegree(degree)
                                    approximationViewModel.loadApproximation(selectedStock)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Model type selection buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModelType.values().forEach { modelType ->
                OutlinedButton(
                    onClick = {
                        selectedModel = modelType
                        when (modelType) {
                            ModelType.APPROXIMATION -> approximationViewModel.loadApproximation(selectedStock)
                            else -> {}
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (selectedModel == modelType) DarkThemeColors.primary else DarkThemeColors.onBackground
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(if (selectedModel == modelType) DarkThemeColors.primary else DarkThemeColors.onBackground.copy(alpha = 0.5f))
                    )
                ) {
                    Text(modelType.displayName)
                }
            }
        }

        // Spacer for spacing
        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                LoadingView(selectedStock)
            }
            error != null -> {
                ErrorView(error!!) {
                    stockViewModel.loadStockData(selectedStock)
                }
            }
            stockData != null -> {
                val modelData = when (selectedModel) {
                    ModelType.APPROXIMATION -> approximationData
                    else -> null
                }

                val modelIsLoading = when (selectedModel) {
                    ModelType.APPROXIMATION -> approxIsLoading
                    else -> false
                }

                val modelColumnName = when (selectedModel) {
                    ModelType.APPROXIMATION -> "Approximation"
                    else -> ""
                }

                if (selectedModel == ModelType.APPROXIMATION && approxError != null) {
                    ErrorView(approxError!!) {
                        approximationViewModel.loadApproximation(selectedStock)
                    }
                } else if (selectedModel != ModelType.NONE && modelIsLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = DarkThemeColors.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Calculating ${selectedModel.displayName} model...",
                                color = DarkThemeColors.onBackground
                            )
                        }
                    }
                } else {
                    DataView(stockData!!, modelData, selectedModel.displayName, modelColumnName)
                }
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

/**
 * Composable function that displays a loading indicator with appropriate text.
 * Shows a circular progress indicator and a message indicating which stock data is being loaded.
 * This view is displayed while data is being fetched from the API.
 *
 * @param stockSymbol The stock symbol for which data is being loaded (e.g., "AAPL", "MSFT").
 */
@Composable
private fun LoadingView(stockSymbol: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = DarkThemeColors.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading data for $stockSymbol...",
                color = DarkThemeColors.onBackground
            )
        }
    }
}

/**
 * Composable function that displays an error message with a retry button.
 * Used when data fetching or processing encounters an error, allowing users to retry the operation.
 *
 * @param errorMessage Detailed error message to be displayed to the user.
 * @param onRetry Callback function to be invoked when the retry button is clicked.
 *                This typically triggers a new attempt to fetch or process data.
 */
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

/**
 * Composable function that displays stock data in both chart and tabular formats.
 * This component is responsible for visualizing:
 * 1. A line chart showing stock price trends using MPAndroidChart
 * 2. A scrollable table showing detailed numerical data
 *
 * When model data is provided, it overlays the model's predictions on the chart
 * and adds an additional column to the table for comparison.
 *
 * @param dataFrame DataFrame containing stock data with columns including "Time" and "Price".
 * @param modelDataFrame Optional DataFrame containing model data (e.g., approximation values).
 *                       When null, only raw stock data is displayed.
 * @param modelName Display name of the model being shown (used in chart legend).
 * @param modelColumnName The column name in modelDataFrame that contains the model values to display.
 */
@Composable
private fun DataView(
    dataFrame: DataFrame<StockEntry>,
    modelDataFrame: DataFrame<*>? = null,
    modelName: String = "Approximation",
    modelColumnName: String = "Approximation"
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                    val dataSets = ArrayList<ILineDataSet>()

                    val stockEntries = ArrayList<Entry>()
                    for (i in 0 until dataFrame.rowsCount()) {
                        val price = dataFrame["Price"][i].toString().toFloatOrNull() ?: 0f
                        stockEntries.add(Entry(i.toFloat(), price))
                    }

                    val stockDataSet = LineDataSet(stockEntries, "Price").apply {
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

                    dataSets.add(stockDataSet)

                    if (modelDataFrame != null) {
                        val modelEntries = ArrayList<Entry>()

                        for (i in 0 until modelDataFrame.rowsCount()) {
                            val modelPrice = modelDataFrame[modelColumnName][i].toString().toFloatOrNull() ?: 0f
                            modelEntries.add(Entry(i.toFloat(), modelPrice))
                        }

                        val modelDataSet = LineDataSet(modelEntries, modelName).apply {
                            color = AndroidColor.rgb(255, 165, 0)
                            valueTextColor = AndroidColor.WHITE
                            lineWidth = 2f
                            setDrawCircles(false)
                            setDrawValues(false)
                            mode = LineDataSet.Mode.CUBIC_BEZIER
                            enableDashedLine(10f, 5f, 0f)
                        }

                        dataSets.add(modelDataSet)
                    }

                    chart.data = LineData(dataSets)
                    chart.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = DarkThemeColors.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
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

                    if (modelDataFrame != null) {
                        Text(
                            text = modelColumnName,
                            fontWeight = FontWeight.Bold,
                            color = DarkThemeColors.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Divider(color = DarkThemeColors.onSurface.copy(alpha = 0.2f))

                val priceFormatter = remember { DecimalFormat("#,##0.00") }

                LazyColumn {
                    items(dataFrame.rowsCount()) { rowIndex ->
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

                            if (modelDataFrame != null && rowIndex < modelDataFrame.rowsCount()) {
                                val modelValue = modelDataFrame[modelColumnName][rowIndex].toString().toDoubleOrNull()
                                Text(
                                    text = if (modelValue != null) priceFormatter.format(modelValue) else "-",
                                    color = Color(0xFFFF9800),
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
        }
    }
}
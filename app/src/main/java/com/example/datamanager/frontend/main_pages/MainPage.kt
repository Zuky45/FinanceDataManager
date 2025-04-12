package com.example.datamanager.frontend.main_pages

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datamanager.R
import com.example.datamanager.mid.main_pages.MainPageModelHandler
import com.example.datamanager.mid.main_pages.StockAction
import com.example.datamanager.mid.main_pages.StockModelHandler
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/**
 * Composable function that represents the main page of the application.
 *
 * @param navController The navigation controller used to navigate between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController) {
    // State for managing the drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedOption by remember { mutableStateOf("Dashboard") }

    // ViewModel to handle main page logic
    val modelHandler: MainPageModelHandler = viewModel()
    val actions by modelHandler.actions.collectAsState()
    val isLoading by modelHandler.isLoading.collectAsState()
    val refreshTime by modelHandler.refreshTime.collectAsState()

    // ViewModel for handling stock data for the graph
    val stockModelHandler: StockModelHandler = viewModel()

    // Define the theme and layout for the main page
    MaterialTheme(colorScheme = customColors) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = customColors.surface
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.app_name),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = customColors.onSurface
                    )
                    Divider(color = customColors.onSurface.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Navigation drawer item for viewing the graph
                    val viewGraph = stringResource(R.string.graph)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.BarChart, null) },
                        label = { Text(viewGraph) },
                        selected = selectedOption == viewGraph,
                        onClick = {
                            selectedOption = viewGraph
                            scope.launch {
                                drawerState.close()
                                navController.navigate("graph")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(selectedOption) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
                            }
                        },
                        actions = {
                            IconButton(onClick = { modelHandler.refreshPrices() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh prices")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = customColors.surface,
                            titleContentColor = customColors.onSurface
                        )
                    )
                },
                containerColor = customColors.background
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Display the last refresh time
                    if (refreshTime.isNotEmpty()) {
                        Text(
                            text = "Last updated: $refreshTime",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Card displaying stock actions
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = customColors.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Stock Actions",
                                style = MaterialTheme.typography.titleMedium,
                                color = customColors.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Show loading indicator if data is being loaded
                            if (isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                // Header row for stock actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Symbol",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        "Price",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                    Text(
                                        "Change",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                // List of stock actions
                                LazyColumn {
                                    items(actions) { action ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                action.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                "$${action.price}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.End
                                            )
                                            Text(
                                                "${if (action.change > 0) "+" else ""}${action.change}%",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (action.change > 0) Color.Green else if (action.change < 0) Color.Red else Color.Gray,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card displaying stock graph
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = customColors.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Stock Performance Overview",
                                style = MaterialTheme.typography.titleMedium,
                                color = customColors.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Show loading indicator if data is being loaded
                            if (isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                // Stock graph component
                                StockGraph(
                                    actions = actions,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable function to display a stock graph using MPAndroidChart.
 *
 * @param actions List of stock actions to display in the graph.
 * @param modifier Modifier to apply to the graph container.
 */
@Composable
fun StockGraph(
    actions: List<StockAction>,
    modifier: Modifier = Modifier
) {
    val modelHandler: MainPageModelHandler = viewModel()
    val stockDataMap by modelHandler.stockDataMap.collectAsState()

    // Display a message if no data is available
    if (actions.isEmpty() || stockDataMap.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available for graph",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        return
    }

    // Use AndroidView to integrate MPAndroidChart's LineChart
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

            val colors = listOf(
                AndroidColor.rgb(66, 134, 244), // Blue
                AndroidColor.rgb(255, 165, 0),  // Orange
                AndroidColor.rgb(76, 175, 80),  // Green
                AndroidColor.rgb(233, 30, 99),  // Pink
                AndroidColor.rgb(156, 39, 176)  // Purple
            )

            actions.forEachIndexed { index, action ->
                val stockData = stockDataMap[action.name] ?: return@forEachIndexed

                try {
                    val prices = stockData["Price"].toList().mapNotNull { it as? Double }
                    if (prices.isEmpty()) return@forEachIndexed

                    val entries = ArrayList<Entry>()
                    prices.forEachIndexed { i, price ->
                        entries.add(Entry(i.toFloat(), price.toFloat()))
                    }

                    val dataSet = LineDataSet(entries, action.name).apply {
                        color = colors[index % colors.size]
                        valueTextColor = AndroidColor.WHITE
                        lineWidth = 2f
                        setDrawCircles(false)
                        setDrawValues(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER

                        if (index == 0) {
                            setDrawFilled(true)
                            fillColor = color
                            fillAlpha = 50
                        }
                    }

                    dataSets.add(dataSet)
                } catch (e: Exception) {
                    Log.e("StockGraph", "Error creating dataset for ${action.name}", e)
                }
            }

            chart.data = LineData(dataSets)
            chart.invalidate()
        },
        modifier = modifier
    )
}






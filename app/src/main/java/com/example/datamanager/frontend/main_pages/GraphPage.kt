package com.example.datamanager.frontend.main_pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datamanager.mid.main_pages.model_handlers.ApproximationModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.MaFiltrationModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.StockModelHandler
import com.example.datamanager.frontend.main_pages.graph_page.*
import com.example.datamanager.mid.main_pages.model_handlers.ArPredictionModelHandler

/**
 * Composable function to display the GraphPage, which includes stock data, model controls, and data visualization.
 *
 * @param navController The NavController used for navigation between screens.
 * @param stockSymbol The default stock symbol to display (default is "AAPL").
 * @param stockViewModel The ViewModel responsible for managing stock data.
 * @param approximationViewModel The ViewModel responsible for managing approximation model data.
 * @param maFiltrationViewModel The ViewModel responsible for managing moving average filtration model data.
 */
@Composable
fun GraphPage(
    navController: NavController,
    stockSymbol: String = "AAPL",
    stockViewModel: StockModelHandler = viewModel(),
) {
    val maFiltrationViewModel = MaFiltrationModelHandler.getInstance()
    val approximationViewModel = ApproximationModelHandler.getInstance()
    val arPredictionViewModel = ArPredictionModelHandler.getInstance()
    // Stock data state
    val stockData by stockViewModel.stockData.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()
    val error by stockViewModel.error.collectAsState()

    // UI state
    val availableStocks = stockViewModel.avaliableActions()
    var selectedStock by remember { mutableStateOf(stockSymbol) }
    var selectedModel by remember { mutableStateOf(ModelType.NONE) }

    // Map of model types to their handlers
    val modelHandlers = remember {
        mapOf(
            ModelType.APPROXIMATION to approximationViewModel,
            ModelType.MAFILTRATION to maFiltrationViewModel,
            ModelType.ARPREDICTION to arPredictionViewModel
        )
    }

    // Get the currently selected model handler
    val currentModelHandler = modelHandlers[selectedModel]

    // Load data when selection changes
    LaunchedEffect(selectedStock, selectedModel) {
        stockViewModel.loadStockData(selectedStock)
        reloadModelData(selectedModel, selectedStock, currentModelHandler)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkThemeColors.background)
            .padding(bottom = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Stock selection UI
        StockSelector(
            availableStocks = availableStocks,
            selectedStock = selectedStock,
            onStockSelected = { selectedStock = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Text(
            text = "Stock Data for $selectedStock",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkThemeColors.onBackground,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Model-specific controls
        ModelControls(
            selectedModel = selectedModel,
            approximationViewModel = approximationViewModel,
            maFiltrationViewModel = maFiltrationViewModel,
            arPredictionViewModel = arPredictionViewModel,
            navController = navController
        )

        // Model type selection
        ModelSelector(
            selectedModel = selectedModel,
            onModelSelected = { selectedModel = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Data display section
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
                // Get model state based on selection
                val modelState = getModelState(
                    selectedModel,
                    approximationViewModel,
                    maFiltrationViewModel,
                    arPredictionViewModel
                )

                if (selectedModel != ModelType.NONE && modelState.isLoading) {
                    ModelLoadingView(modelState.displayName)
                } else if (selectedModel != ModelType.NONE && modelState.error != null) {
                    ErrorView(modelState.error!!) {
                        reloadModelData(selectedModel, selectedStock, currentModelHandler)
                    }
                } else {
                    DataView(
                        dataFrame = stockData!!,
                        modelDataFrame = modelState.data,
                        modelName = modelState.displayName,
                        modelColumnName = modelState.columnName
                    )
                }
            }
            else -> {
                NoDataView()
            }
        }
    }
}

/**
 * Retrieves the UI state for the selected model type.
 *
 * @param modelType The type of the model (e.g., APPROXIMATION, MAFILTRATION).
 * @param approximationViewModel The ViewModel responsible for managing approximation model data.
 * @param maFiltrationViewModel The ViewModel responsible for managing moving average filtration model data.
 * @return A ModelUIState object containing the data, loading state, error, column name, and display name for the model.
 */
@Composable
private fun getModelState(
    modelType: ModelType,
    approximationViewModel: ApproximationModelHandler,
    maFiltrationViewModel: MaFiltrationModelHandler,
    arPredictionViewModel: ArPredictionModelHandler
): ModelUIState {
    return when (modelType) {
        ModelType.APPROXIMATION -> {
            val data by approximationViewModel.approximationData.collectAsState()
            val isLoading by approximationViewModel.isLoading.collectAsState()
            val error by approximationViewModel.error.collectAsState()
            ModelUIState(
                data = data,
                isLoading = isLoading,
                error = error,
                columnName = modelType.getColumnName(),
                displayName = modelType.displayName
            )
        }
        ModelType.MAFILTRATION -> {
            val data by maFiltrationViewModel.maFiltrationData.collectAsState()
            val isLoading by maFiltrationViewModel.isLoading.collectAsState()
            val error by maFiltrationViewModel.error.collectAsState()
            ModelUIState(
                data = data,
                isLoading = isLoading,
                error = error,
                columnName = modelType.getColumnName(),
                displayName = modelType.displayName
            )
        }
        ModelType.ARPREDICTION -> {
            val data by arPredictionViewModel.arPredictionData.collectAsState()
            val isLoading by arPredictionViewModel.isLoading.collectAsState()
            val error by arPredictionViewModel.error.collectAsState()
            ModelUIState(
                data = data,
                isLoading = isLoading,
                error = error,
                columnName = modelType.getColumnName(),
                displayName = modelType.displayName
            )
        }
        else -> ModelUIState(
            data = null,
            isLoading = false,
            error = null,
            columnName = "",
            displayName = modelType.displayName
        )
    }
}
/**
 * This file contains composable functions for displaying model controls in the graph page.
 * It provides user interface elements for selecting parameters and interacting with different
 * analytical models (Approximation, Moving Average Filtration, and AR Prediction).
 */
package com.example.datamanager.frontend.main_pages.graph_page

import android.media.MediaRouter2.RoutingController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datamanager.R
import com.example.datamanager.frontend.navigations.NavigationRoutes
import com.example.datamanager.mid.main_pages.model_handlers.ApproximationModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.ArPredictionModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.MaFiltrationModelHandler

/**
 * Composable function to display controls for the selected model.
 *
 * @param selectedModel The currently selected model type.
 * @param approximationViewModel The ViewModel for handling approximation model logic.
 * @param maFiltrationViewModel The ViewModel for handling moving average filtration logic.
 * @param arPredictionViewModel The ViewModel for handling AR prediction model logic.
 * @param navController The navigation controller for handling navigation actions.
 */
@Composable
fun ModelControls(
    selectedModel: ModelType,
    approximationViewModel: ApproximationModelHandler,
    maFiltrationViewModel: MaFiltrationModelHandler,
    arPredictionViewModel: ArPredictionModelHandler,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Button to navigate back to the previous screen
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.primary)
        ) {
            Text(stringResource(R.string.back))
        }

        // Display controls based on the selected model type
        when (selectedModel) {
            ModelType.APPROXIMATION -> {
                ApproximationControls(approximationViewModel, navController)
            }
            ModelType.MAFILTRATION -> {
                MaFiltrationControls(maFiltrationViewModel, navController)
            }
            ModelType.ARPREDICTION -> {
                ArPredictionControls(arPredictionViewModel, navController)
            }
            else -> {}
        }
    }
}

/**
 * Composable function to display controls for the approximation model.
 *
 * @param viewModel The ViewModel for handling approximation model logic.
 * @param navController The navigation controller for handling navigation to details page.
 */
@Composable
fun ApproximationControls(
    viewModel: ApproximationModelHandler,
    navController: NavController
) {
    val degreeOptions = listOf(1,2, 3, 4, 5, 6) // Available degree options for the model
    var expandedDegree by remember { mutableStateOf(false) } // State to track dropdown menu visibility
    val selectedDegree by viewModel.degree.collectAsState() // Currently selected degree
    val approximationData by viewModel.approximationData.collectAsState() // Current approximation data
    val coefficients by viewModel.coefficients.collectAsState() // Current coefficients

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            // Button to display the dropdown menu for selecting a degree
            OutlinedButton(
                onClick = { expandedDegree = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkThemeColors.onBackground
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(DarkThemeColors.onBackground)
                )
            ) {
                Text(stringResource(R.string.degree_label, selectedDegree))
            }

            // Dropdown menu for selecting a degree
            DropdownMenu(
                expanded = expandedDegree,
                onDismissRequest = { expandedDegree = false }
            ) {
                degreeOptions.forEach { degree ->
                    DropdownMenuItem(
                        text = { Text("$degree") },
                        onClick = {
                            viewModel.setDegree(degree) // Update the selected degree in the ViewModel
                            expandedDegree = false
                        }
                    )
                }
            }
        }

        // Details button - only enabled when approximation data is available
        Button(
            onClick = {
                navController.navigate(NavigationRoutes.APPROXIMATION_DETAILS)
            },
            enabled = approximationData != null && coefficients != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkThemeColors.secondary,
                disabledContainerColor = DarkThemeColors.secondary.copy(alpha = 0.5f)
            )
        ) {
            Text(stringResource(R.string.details))
        }
    }
}

/**
 * Composable function to display controls for the moving average filtration model.
 *
 * @param viewModel The ViewModel for handling moving average filtration logic.
 * @param navController The navigation controller for handling navigation to details page.
 */
@Composable
fun MaFiltrationControls(
    viewModel: MaFiltrationModelHandler,
    navController: NavController
) {
    val windowSizeOptions = listOf(2, 3, 5, 7, 10, 14, 21) // Available window size options for the model
    var expandedWindowSize by remember { mutableStateOf(false) } // State to track dropdown menu visibility
    val selectedWindowSize by viewModel.windowSize.collectAsState() // Currently selected window size
    val filtrationData by viewModel.maFiltrationData.collectAsState() // Current filtration data
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            // Button to display the dropdown menu for selecting a window size
            OutlinedButton(
                onClick = { expandedWindowSize = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkThemeColors.onBackground
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(DarkThemeColors.onBackground)
                )
            ) {
                Text(stringResource(R.string.window_size_label, selectedWindowSize))
            }

            // Dropdown menu for selecting a window size
            DropdownMenu(
                expanded = expandedWindowSize,
                onDismissRequest = { expandedWindowSize = false }
            ) {
                windowSizeOptions.forEach { size ->
                    DropdownMenuItem(
                        text = { Text("$size") },
                        onClick = {
                            viewModel.setWindowSize(size) // Update the selected window size in the ViewModel
                            expandedWindowSize = false
                        }
                    )

                }
            }

        }
        Button(
            onClick = {
                navController.navigate(NavigationRoutes.FILTRATION_DETAILS)
            },
            enabled = filtrationData != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkThemeColors.secondary,
                disabledContainerColor = DarkThemeColors.secondary.copy(alpha = 0.5f)
            )
        ) {
            Text(stringResource(R.string.details))
        }
    }
}

/**
 * Composable function to display controls for the AR (Auto-Regressive) prediction model.
 * Provides UI elements for adjusting model parameters and navigating to detailed results.
 *
 * @param viewModel The ViewModel for handling AR prediction model logic.
 * @param navController The navigation controller for handling navigation to details page.
 */
@Composable
fun ArPredictionControls(
    viewModel: ArPredictionModelHandler,
    navController: NavController
) {
    val orderOptions = (1..30).toList() // Available order options for the AR model
    val horizonOptions = listOf(5, 10, 15, 20, 30) // Available prediction horizon options
    var expandedOrder by remember { mutableStateOf(false) } // State to track order dropdown menu visibility
    var expandedHorizon by remember { mutableStateOf(false) } // State to track horizon dropdown menu visibility
    val selectedOrder by viewModel.order.collectAsState() // Currently selected AR model order
    val selectedHorizon by viewModel.predictionHorizon.collectAsState() // Currently selected prediction horizon
    val predictionData by viewModel.arPredictionData.collectAsState() // Current prediction data
    val coefficients by viewModel.coefficients.collectAsState() // Current AR model coefficients

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Order dropdown
        Box {
            OutlinedButton(
                onClick = { expandedOrder = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkThemeColors.onBackground
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(DarkThemeColors.onBackground)
                )
            ) {
                Text(stringResource(R.string.order_label, selectedOrder))
            }

            DropdownMenu(
                expanded = expandedOrder,
                onDismissRequest = { expandedOrder = false }
            ) {
                orderOptions.forEach { order ->
                    DropdownMenuItem(
                        text = { Text("$order") },
                        onClick = {
                            viewModel.setOrder(order)
                            expandedOrder = false
                        }
                    )
                }
            }
        }

        // Horizon dropdown
        Box {
            OutlinedButton(
                onClick = { expandedHorizon = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkThemeColors.onBackground
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(DarkThemeColors.onBackground)
                )
            ) {
                Text(stringResource(R.string.horizon_label, selectedHorizon))
            }

            DropdownMenu(
                expanded = expandedHorizon,
                onDismissRequest = { expandedHorizon = false }
            ) {
                horizonOptions.forEach { horizon ->
                    DropdownMenuItem(
                        text = { Text("$horizon") },
                        onClick = {
                            viewModel.setPredictionHorizon(horizon)
                            expandedHorizon = false
                        }
                    )
                }
            }
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Details button - only enabled when prediction data and coefficients are available
        Button(
            onClick = {
                navController.navigate(NavigationRoutes.AR_PREDICTION_DETAILS)
            },
            enabled = predictionData != null && coefficients != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkThemeColors.secondary,
                disabledContainerColor = DarkThemeColors.secondary.copy(alpha = 0.5f)
            )
        ) {
            Text(stringResource(R.string.details))
        }
    }
}
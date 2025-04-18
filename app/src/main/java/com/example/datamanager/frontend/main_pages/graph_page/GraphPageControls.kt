package com.example.datamanager.frontend.main_pages.graph_page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datamanager.mid.main_pages.ApproximationModelHandler
import com.example.datamanager.mid.main_pages.MaFiltrationModelHandler

/**
 * Composable function to display controls for the selected model.
 *
 * @param selectedModel The currently selected model type.
 * @param approximationViewModel The ViewModel for handling approximation model logic.
 * @param maFiltrationViewModel The ViewModel for handling moving average filtration logic.
 * @param navController The navigation controller for handling navigation actions.
 */
@Composable
fun ModelControls(
    selectedModel: ModelType,
    approximationViewModel: ApproximationModelHandler,
    maFiltrationViewModel: MaFiltrationModelHandler,
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
            Text("Back")
        }

        // Display controls based on the selected model type
        when (selectedModel) {
            ModelType.APPROXIMATION -> {
                ApproximationControls(approximationViewModel, navController)
            }
            ModelType.MAFILTRATION -> {
                MaFiltrationControls(maFiltrationViewModel)
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
    val degreeOptions = listOf(2, 3, 4, 5, 6) // Available degree options for the model
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
                Text("Degree: $selectedDegree")
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
                navController.navigate("approximation_details")
            },
            enabled = approximationData != null && coefficients != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkThemeColors.secondary,
                disabledContainerColor = DarkThemeColors.secondary.copy(alpha = 0.5f)
            )
        ) {
            Text("Details")
        }
    }
}

/**
 * Composable function to display controls for the moving average filtration model.
 *
 * @param viewModel The ViewModel for handling moving average filtration logic.
 */
@Composable
fun MaFiltrationControls(viewModel: MaFiltrationModelHandler) {
    val windowSizeOptions = listOf(2, 3, 5, 7, 10, 14, 21) // Available window size options for the model
    var expandedWindowSize by remember { mutableStateOf(false) } // State to track dropdown menu visibility
    val selectedWindowSize by viewModel.windowSize.collectAsState() // Currently selected window size

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
            Text("Window Size: $selectedWindowSize")
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
}
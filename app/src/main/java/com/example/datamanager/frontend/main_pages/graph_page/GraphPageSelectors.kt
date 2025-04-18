package com.example.datamanager.frontend.main_pages.graph_page

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

/**
 * Composable function to display a stock selector dropdown menu.
 *
 * @param availableStocks A list of available stock names to choose from.
 * @param selectedStock The currently selected stock name.
 * @param onStockSelected A callback function invoked when a stock is selected, passing the selected stock name.
 */
@Composable
fun StockSelector(
    availableStocks: List<String>,
    selectedStock: String,
    onStockSelected: (String) -> Unit
) {
    var expandedStockMenu by remember { mutableStateOf(false) } // Tracks the visibility of the dropdown menu

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box {
            // Button to open the dropdown menu
            Button(
                onClick = { expandedStockMenu = true },
                colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.primary)
            ) {
                Text("Select Stock: $selectedStock")
            }

            // Dropdown menu displaying the list of available stocks
            DropdownMenu(
                expanded = expandedStockMenu,
                onDismissRequest = { expandedStockMenu = false }
            ) {
                availableStocks.forEach { stock ->
                    DropdownMenuItem(
                        text = { Text(stock) },
                        onClick = {
                            onStockSelected(stock) // Notify the parent of the selected stock
                            expandedStockMenu = false // Close the dropdown menu
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable function to display a model selector with buttons for each model type.
 *
 * @param selectedModel The currently selected model type.
 * @param onModelSelected A callback function invoked when a model type is selected, passing the selected model type.
 */
@Composable
fun ModelSelector(
    selectedModel: ModelType,
    onModelSelected: (ModelType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .horizontalScroll(rememberScrollState()), // Enables horizontal scrolling for the row
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Adds spacing between buttons
    ) {
        ModelType.values().forEach { modelType ->
            // Button for each model type
            OutlinedButton(
                onClick = { onModelSelected(modelType) }, // Notify the parent of the selected model type
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (modelType == selectedModel)
                        DarkThemeColors.primary else DarkThemeColors.onBackground
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(if (modelType == selectedModel)
                        DarkThemeColors.primary else DarkThemeColors.onBackground)
                )
            ) {
                Text(modelType.displayName) // Display the model type's name
            }
        }
    }
}
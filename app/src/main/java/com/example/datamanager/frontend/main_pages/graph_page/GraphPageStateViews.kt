// Documentation partially generated
// Refactoring done with copilot
// Some functionality implemented with use of copilot
// @ author: Michal Poprac
/**
 * This file contains composable functions for displaying different state views in the graph page.
 * It provides UI components for loading, error, and empty states for both stock data and models.
 */
package com.example.datamanager.frontend.main_pages.graph_page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.datamanager.R

/**
 * Composable function to display a loading view with a progress indicator and a message.
 *
 * @param stockSymbol The stock symbol for which data is being loaded.
 */
@Composable
fun LoadingView(stockSymbol: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Circular progress indicator to show loading state
            CircularProgressIndicator(color = DarkThemeColors.primary)
            Spacer(modifier = Modifier.height(16.dp))
            // Text message indicating the stock being loaded
            Text(
                text = stringResource(R.string.loading_stock_data, stockSymbol),
                color = DarkThemeColors.onBackground
            )
        }
    }
}

/**
 * Composable function to display a loading view for a model calculation.
 *
 * @param modelName The name of the model being calculated.
 */
@Composable
fun ModelLoadingView(modelName: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Circular progress indicator to show loading state
            CircularProgressIndicator(color = DarkThemeColors.primary)
            Spacer(modifier = Modifier.height(16.dp))
            // Text message indicating the model being calculated
            Text(
                text = stringResource(R.string.calculating_model, modelName),
                color = DarkThemeColors.onBackground
            )
        }
    }
}

/**
 * Composable function to display a view indicating that no data is available.
 */
@Composable
fun NoDataView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Text message indicating no data is available
        Text(
            text = stringResource(R.string.no_data_available),
            color = DarkThemeColors.error
        )
    }
}

/**
 * Composable function to display an error view with a retry button.
 *
 * @param errorMessage The error message to display.
 * @param onRetry A callback function invoked when the retry button is clicked.
 */
@Composable
fun ErrorView(errorMessage: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Text message displaying the error
            Text(
                text = errorMessage,
                color = DarkThemeColors.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Retry button to allow the user to retry the action
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.primary)
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}
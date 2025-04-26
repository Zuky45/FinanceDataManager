/**
 * This file contains the composable for the Alerts Page.
 * It allows users to create, view, and manage stock price alerts,
 * as well as view alerts that have been triggered.
 */
package com.example.datamanager.frontend.main_pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datamanager.R
import com.example.datamanager.frontend.main_pages.graph_page.DarkThemeColors
import com.example.datamanager.mid.main_pages.AlertsModelHandler

/**
 * Composable function for the Alerts Page.
 * Displays stock price alerts, allows users to create new alerts, view existing alerts, and see triggered alerts.
 *
 * @param navController The NavController for handling navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsPage(navController: NavController) {
    val context = LocalContext.current
    val viewModel = remember { AlertsModelHandler(context) }

    // Collect state flows from the ViewModel
    val availableStocks by viewModel.availableActions.collectAsState()
    val currentPrices by viewModel.currentPrices.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val triggeredAlerts by viewModel.triggeredAlerts.collectAsState()

    // State variables for alert creation, saved across configuration changes
    var selectedStock by rememberSaveable { mutableStateOf("") }
    var alertPrice by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Load alerts and current prices when the composable is first launched
    LaunchedEffect(Unit) {
        viewModel.loadAlerts()
        viewModel.loadCurrentPrices()
    }

    // Main layout container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkThemeColors.background)
            .padding(16.dp, top = 32.dp)
    ) {
        // Header with a back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {navController.popBackStack()}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    tint = DarkThemeColors.onBackground
                )
            }
            Text(
                text = stringResource(R.string.stock_price_alerts_title),
                style = MaterialTheme.typography.headlineMedium,
                color = DarkThemeColors.onBackground,
                modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
            )
        }

        // Section for creating a new alert
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkThemeColors.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_new_alert),
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkThemeColors.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Dropdown menu for selecting a stock
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedStock,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_stock), color = DarkThemeColors.onSurface.copy(alpha = 0.7f)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = TextFieldDefaults.textFieldColors(
                            unfocusedTextColor = DarkThemeColors.onSurface,
                            focusedTextColor = DarkThemeColors.onSurface,
                            containerColor = DarkThemeColors.surfaceVariant,
                            cursorColor = DarkThemeColors.primary,
                            focusedIndicatorColor = DarkThemeColors.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    // Dropdown menu items
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(DarkThemeColors.surfaceVariant)
                    ) {
                        availableStocks.forEach { stock ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "$stock (${currentPrices[stock]?.let { "$$it" } ?: stringResource(R.string.price_not_available)})",
                                        color = DarkThemeColors.onSurface
                                    )
                                },
                                onClick = {
                                    selectedStock = stock
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Text field for entering the alert price
                TextField(
                    value = alertPrice,
                    onValueChange = { alertPrice = it },
                    label = { Text(stringResource(R.string.alert_price_label), color = DarkThemeColors.onSurface.copy(alpha = 0.7f)) },
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedTextColor = DarkThemeColors.onSurface,
                        focusedTextColor = DarkThemeColors.onSurface,
                        containerColor = DarkThemeColors.surfaceVariant,
                        cursorColor = DarkThemeColors.primary,
                        focusedIndicatorColor = DarkThemeColors.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button to add a new alert
                Button(
                    onClick = {
                        if (selectedStock.isNotEmpty() && alertPrice.isNotEmpty()) {
                            val price = alertPrice.toDoubleOrNull()
                            if (price != null) {
                                viewModel.addAlert(selectedStock, price)
                                selectedStock = ""
                                alertPrice = ""
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = selectedStock.isNotEmpty() && alertPrice.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkThemeColors.primary,
                        contentColor = DarkThemeColors.onBackground
                    )
                ) {
                    Text(stringResource(R.string.add_alert))
                }
            }
        }

        // Section for displaying existing alerts
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkThemeColors.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.your_alerts),
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkThemeColors.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Button to clear all alerts
                    Button(
                        onClick = { viewModel.clearAllAlerts() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkThemeColors.error,
                            contentColor = DarkThemeColors.onBackground
                        )
                    ) {
                        Text(stringResource(R.string.clear_all))
                    }
                }

                // Display a message if no alerts are set
                if (alerts.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_alerts_set),
                        color = DarkThemeColors.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    // List of existing alerts
                    LazyColumn {
                        items(alerts) { alert ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = alert.symbol,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkThemeColors.onSurface
                                    )
                                    Row {
                                        Text(stringResource(R.string.alert_price_format, alert.price),
                                            color = DarkThemeColors.onSurface.copy(alpha = 0.8f))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        currentPrices[alert.symbol]?.let {
                                            Text(stringResource(R.string.current_price_format, it),
                                                color = DarkThemeColors.onSurface.copy(alpha = 0.8f))
                                        }
                                    }
                                }

                                // Button to delete an alert
                                IconButton(onClick = { viewModel.deleteAlert(alert.symbol) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete_alert),
                                        tint = DarkThemeColors.error
                                    )
                                }
                            }
                            Divider(color = DarkThemeColors.onSurface.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }

        // Section for displaying triggered alerts
        if (triggeredAlerts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkThemeColors.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.triggered_alerts),
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkThemeColors.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // List of triggered alerts
                    LazyColumn {
                        items(triggeredAlerts) { alert ->
                            Text(
                                text = alert,
                                color = DarkThemeColors.error,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
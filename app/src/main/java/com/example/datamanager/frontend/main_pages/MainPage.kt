package com.example.datamanager.frontend.main_pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datamanager.R
import com.example.datamanager.mid.main_pages.MainPageModelHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedOption by remember { mutableStateOf("Dashboard") }

    val modelHandler: MainPageModelHandler = viewModel()
    val actions by modelHandler.actions.collectAsState()
    val isLoading by modelHandler.isLoading.collectAsState()
    val refreshTime by modelHandler.refreshTime.collectAsState()

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
                    if (refreshTime.isNotEmpty()) {
                        Text(
                            text = "Last updated: $refreshTime",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                                // Header row
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
                }
            }
        }
    }
}




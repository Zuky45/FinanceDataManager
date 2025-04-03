package com.example.datamanager.frontend.main_pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datamanager.R
import kotlinx.coroutines.launch

/**
 * MainPage composable function that displays the main content of the app.
 * It includes a navigation drawer and a top app bar.
 *
 * @param navController The NavController used for navigation between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController) {
    // State for the drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Coroutine scope for launching coroutines
    val scope = rememberCoroutineScope()
    // State for the selected option in the drawer
    var selectedOption by remember { mutableStateOf("") }

    // Custom MaterialTheme with custom colors
    MaterialTheme(colorScheme = customColors) {
        // ModalNavigationDrawer with drawer content
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

                    // Navigation drawer items
                    val chooseData = stringResource(R.string.choose_data)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.DateRange, null) },
                        label = { Text(chooseData) },
                        selected = selectedOption == chooseData,
                        onClick = {
                            selectedOption = chooseData
                            scope.launch {
                                drawerState.close()
                                navController.navigate("choose_data")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    val graph = stringResource(R.string.graph)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.DateRange, null) },
                        label = { Text(graph) },
                        selected = selectedOption == graph,
                        onClick = {
                            selectedOption = graph
                            scope.launch {
                                drawerState.close()
                                navController.navigate("graph")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    val mathModels = stringResource(R.string.math_models)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.DateRange, null) },
                        label = { Text(mathModels) },
                        selected = selectedOption == mathModels,
                        onClick = {
                            selectedOption = mathModels
                            scope.launch {
                                drawerState.close()
                                navController.navigate("math_models")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    val settings = stringResource(R.string.settings)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text(settings) },
                        selected = selectedOption == settings,
                        onClick = {
                            selectedOption = settings
                            scope.launch {
                                drawerState.close()
                                navController.navigate("settings")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    val logout = stringResource(R.string.logout)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Clear, null) },
                        label = { Text(logout) },
                        selected = selectedOption == logout,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("login")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                    )
                }
            }
        ) {
            // Scaffold with top app bar and main content
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(selectedOption) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.main, selectedOption),
                        color = customColors.onBackground
                    )
                }
            }
        }
    }
}




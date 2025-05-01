package com.example.datamanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datamanager.frontend.login_pages.LoginScreen
import com.example.datamanager.frontend.login_pages.NewAccountPage
import com.example.datamanager.frontend.main_pages.AlertsPage
import com.example.datamanager.frontend.main_pages.model_details.ApproximationDetailsPage
import com.example.datamanager.frontend.main_pages.model_details.ArPredictionDetailsPage
import com.example.datamanager.frontend.main_pages.GraphPage
import com.example.datamanager.frontend.main_pages.model_details.MaFiltrationDetailsPage
import com.example.datamanager.frontend.main_pages.MainPage
import com.example.datamanager.frontend.navigations.NavigationRoutes
import com.example.datamanager.mid.main_pages.model_handlers.ApproximationModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.ArPredictionModelHandler
import com.example.datamanager.mid.main_pages.model_handlers.MaFiltrationModelHandler

/**
 * MainActivity is the entry point of the application.
 * It sets up the navigation structure and initializes the UI using Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     * Sets the content view to the main navigation structure.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }

    /**
     * MainContent sets up the navigation host and defines the navigation routes for the app.
     * It uses Jetpack Compose's NavHost to manage navigation between different screens.
     */
    @Composable
    fun MainContent() {
        val navController = rememberNavController() // Creates a NavController to manage app navigation.
        NavHost(navController = navController, startDestination = NavigationRoutes.LOGIN) {
            // Defines the navigation route for the main page.
            composable(NavigationRoutes.MAIN) { MainPage(navController) }
            // Defines the navigation route for the login screen.
            composable(NavigationRoutes.LOGIN) { LoginScreen(navController) }
            // Defines the navigation route for the new account creation page.
            composable(NavigationRoutes.NEW_ACCOUNT) { NewAccountPage(navController) }
            // Defines the navigation route for the graph page.
            composable(NavigationRoutes.GRAPH) { GraphPage(navController) }
            // Defines the navigation route for the approximation details page.
            composable(NavigationRoutes.APPROXIMATION_DETAILS) {
                ApproximationDetailsPage(
                    navController = navController,
                    approximationHandler = ApproximationModelHandler.getInstance()
                )
            }
            // Defines the navigation route for the moving average filtration details page.
            composable(NavigationRoutes.FILTRATION_DETAILS) {
                MaFiltrationDetailsPage(
                    navController = navController,
                    maFiltrationModelHandler = MaFiltrationModelHandler.getInstance()
                )
            }
            // Defines the navigation route for the AR prediction details page.
            composable(NavigationRoutes.AR_PREDICTION_DETAILS) {
                ArPredictionDetailsPage(
                    arPredictionHandler = ArPredictionModelHandler.getInstance(),
                    navController = navController
                )
            }
            // Defines the navigation route for the alerts page.
            composable(NavigationRoutes.ALERTS) { AlertsPage(navController) }
        }
    }
}






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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }

    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = NavigationRoutes.LOGIN) {
            composable(NavigationRoutes.MAIN) { MainPage(navController) }
            composable(NavigationRoutes.LOGIN) { LoginScreen(navController) }
            composable(NavigationRoutes.NEW_ACCOUNT) { NewAccountPage(navController) }
            composable(NavigationRoutes.GRAPH) { GraphPage(navController) }
            composable(NavigationRoutes.APPROXIMATION_DETAILS) {
                ApproximationDetailsPage(
                    navController = navController,
                    approximationHandler = ApproximationModelHandler.getInstance()
                )
            }
            composable(NavigationRoutes.FILTRATION_DETAILS) {
                MaFiltrationDetailsPage(
                    navController = navController,
                    maFiltrationModelHandler = MaFiltrationModelHandler.getInstance()
                )
            }
            composable(NavigationRoutes.AR_PREDICTION_DETAILS) {
                ArPredictionDetailsPage(
                    arPredictionHandler = ArPredictionModelHandler.getInstance(),
                    navController = navController
                )
            }
            composable(NavigationRoutes.ALERTS) { AlertsPage(navController) }
        }
    }
}






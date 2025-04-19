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
import com.example.datamanager.frontend.main_pages.model_details.ApproximationDetailsPage
import com.example.datamanager.frontend.main_pages.model_details.ArPredictionDetailsPage
import com.example.datamanager.frontend.main_pages.GraphPage
import com.example.datamanager.frontend.main_pages.model_details.MaFiltrationDetailsPage
import com.example.datamanager.frontend.main_pages.MainPage
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
        NavHost(navController = navController, startDestination = "login") {
            composable("main") { MainPage(navController) }
            composable("login") { LoginScreen(navController) }
            composable("new_account") { NewAccountPage(navController) }
            composable("graph") { GraphPage(navController) }
            composable("approximation_details") {
                ApproximationDetailsPage(
                    navController = navController,
                    approximationHandler = ApproximationModelHandler.getInstance()
                )
            }
            composable("filtration_details") {
                MaFiltrationDetailsPage(
                    navController = navController,
                    maFiltrationModelHandler = MaFiltrationModelHandler.getInstance()
                )
            }
            composable("ar_prediction_details") {
                ArPredictionDetailsPage(
                    arPredictionHandler = ArPredictionModelHandler.getInstance(),
                    navController = navController
                )
            }
        }
    }
}






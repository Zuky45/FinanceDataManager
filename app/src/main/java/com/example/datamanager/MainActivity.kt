package com.example.datamanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datamanager.frontend.login_pages.LoginScreen
import com.example.datamanager.frontend.login_pages.NewAccountPage
import com.example.datamanager.frontend.main_pages.ApproximationDetailsPage
import com.example.datamanager.frontend.main_pages.GraphPage
import com.example.datamanager.frontend.main_pages.MainPage
import com.example.datamanager.mid.main_pages.ApproximationModelHandler

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

        }
    }
}






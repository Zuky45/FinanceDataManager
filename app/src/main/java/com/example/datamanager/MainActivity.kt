package com.example.datamanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datamanager.backend.api_manager.ApiManager
import com.example.datamanager.backend.api_manager.StockEntry
import com.example.datamanager.login_pages.LoginScreen
import com.example.datamanager.login_pages.NewAccountPage
import com.example.datamanager.main_pages.GraphPage
import com.example.datamanager.main_pages.MainPage
import com.example.datamanager.ui.theme.DataManagerTheme
import io.swagger.annotations.Api
import org.jetbrains.kotlinx.dataframe.DataFrame



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DataManagerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("main") { MainPage(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("new_account") { NewAccountPage(navController) }
                    composable("graph") {GraphPage(navController) }
                }
            }
        }
    }
}




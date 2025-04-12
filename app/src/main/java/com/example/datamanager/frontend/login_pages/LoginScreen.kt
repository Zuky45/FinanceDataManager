package com.example.datamanager.frontend.login_pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datamanager.R
import com.example.datamanager.mid.login_pages.LoginModelHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {
    val modelHandler: LoginModelHandler = viewModel()
    val email by modelHandler.email.collectAsState()
    val password by modelHandler.password.collectAsState()
    val isLoading by modelHandler.isLoading.collectAsState()
    val loginSuccess by modelHandler.loginSuccess.collectAsState()
    val invalidUser by modelHandler.invalidUser.collectAsState()
    var isLoginError by rememberSaveable { mutableStateOf(false) }

    // Handle successful login
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            navController.navigate("main")
            modelHandler.resetState()
        }
    }

    MaterialTheme(colorScheme = customColors) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        modelHandler.updateEmail(it)
                        isLoginError = false
                    },
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                    isError = email.isNotEmpty() && !modelHandler.isEmailValid(email),
                    supportingText = {
                        if (email.isNotEmpty() && !modelHandler.isEmailValid(email)) {
                            Text(stringResource(R.string.invalid_email), color = MaterialTheme.colorScheme.error)

                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Email, stringResource(R.string.icon_email)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        modelHandler.updatePassword(it)
                        isLoginError = false
                    },
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isLoginError,
                    supportingText = {

                        if (invalidUser) {
                            Text(
                                text = stringResource(R.string.invalid_credentials),
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                    },
                    leadingIcon = { Icon(Icons.Default.Lock, stringResource(R.string.icon_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                Button(
                    onClick = {
                        if (modelHandler.validateCredentials(email, password)) {
                            modelHandler.login()
                        } else {
                            isLoginError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = modelHandler.isFormValid(email, password) && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.login))
                    }
                }

                TextButton(
                    onClick = { navController.navigate("new_account") },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.create_new_account))
                }

                AnimatedVisibility(visible = isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}























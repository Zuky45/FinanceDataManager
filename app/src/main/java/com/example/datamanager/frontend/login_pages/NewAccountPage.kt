// Documentation partially generated
// Refactoring done with copilot
// Some functionality implemented with use of copilot
// @ author: Michal Poprac
package com.example.datamanager.frontend.login_pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datamanager.R
import com.example.datamanager.frontend.navigations.NavigationRoutes
import com.example.datamanager.mid.login_pages.NewAccountModelHandler

/**
 * Composable function that represents the "Create New Account" screen.
 *
 * This screen provides input fields for the user to enter their email, password,
 * and confirm the password. It also includes a button to toggle password visibility,
 * a button to create the account, and a navigation button to return to the login screen.
 * The screen handles validation, error states, and account creation logic.
 *
 * @param navController The navigation controller used to navigate between screens.
 * @param modifier The modifier to be applied to the root composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAccountPage(navController: NavController, modifier: Modifier = Modifier) {
    // ViewModel to handle account creation logic
    val modelHandler: NewAccountModelHandler = viewModel()
    val email by modelHandler.email.collectAsState()
    val password by modelHandler.password.collectAsState()
    val confirmPassword by modelHandler.confirmPassword.collectAsState()
    val passwordVisibility by modelHandler.passwordVisibility.collectAsState()
    val isLoading by modelHandler.isLoading.collectAsState()
    val accountCreated by modelHandler.accountCreated.collectAsState()
    val accountError by modelHandler.accountError.collectAsState()

    // Define the theme and layout for the "Create New Account" screen
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
                // Title text for the screen
                Text(
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Email input field
                OutlinedTextField(
                    value = email,
                    onValueChange = { modelHandler.updateEmail(it) },
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                    isError = email.isNotEmpty() && !modelHandler.isEmailValid(email),
                    supportingText = {
                        if (email.isNotEmpty() && !modelHandler.isEmailValid(email)) {
                            Text(stringResource(R.string.invalid_email))
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Email, stringResource(R.string.icon_email)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Password input field
                OutlinedTextField(
                    value = password,
                    onValueChange = { modelHandler.updatePassword(it) },
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = password.isNotEmpty() && !modelHandler.passwordsMatch(password, confirmPassword),
                    supportingText = {
                        if (confirmPassword.isNotEmpty() && !modelHandler.passwordsMatch(password, confirmPassword)) {
                            Text(stringResource(R.string.passwords_not_match))
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, stringResource(R.string.icon_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Confirm password input field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { modelHandler.updateConfirmPassword(it) },
                    label = { Text(stringResource(R.string.confirm_password)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirmPassword.isNotEmpty() && !modelHandler.passwordsMatch(password, confirmPassword),
                    supportingText = {
                        if (confirmPassword.isNotEmpty() && !modelHandler.passwordsMatch(password, confirmPassword)) {
                            Text(stringResource(R.string.passwords_not_match))
                        } else if (password.isNotEmpty() && !modelHandler.passwordValid(password)) {
                            Text(stringResource(R.string.password_invalid), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, stringResource(R.string.icon_confirm_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                // Toggle password visibility button
                IconButton(
                    onClick = { modelHandler.togglePasswordVisibility() },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = (if (passwordVisibility) R.string.hide_password.toString() else R.string.show_password).toString()
                    )
                }

                // Create account button
                Button(
                    onClick = { modelHandler.createAccount() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = modelHandler.isEmailValid(email) && modelHandler.passwordsMatch(password, confirmPassword) &&
                            password.isNotEmpty() && confirmPassword.isNotEmpty() && !isLoading &&
                            !accountCreated && modelHandler.passwordValid(password),
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
                    } else if (accountCreated) {
                        Text(stringResource(R.string.account_created))
                    } else if (accountError) {
                        Text(stringResource(R.string.account_creation_failed))
                    } else {
                        Text(stringResource(R.string.create_account))
                    }
                }

                // Button to navigate back to the login screen
                TextButton(
                    onClick = { navController.navigate(NavigationRoutes.LOGIN) },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.back_to_login))
                    }
                }

                // Loading indicator displayed when account creation is in progress
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
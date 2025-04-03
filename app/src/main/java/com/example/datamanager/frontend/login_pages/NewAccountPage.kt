package com.example.datamanager.frontend.login_pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datamanager.R
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAccountPage(navController: NavController, modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var password2 by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

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
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                    isError = email.isNotEmpty() && !isEmailValid(email),
                    supportingText = {
                        if (email.isNotEmpty() && !isEmailValid(email)) {
                            Text(stringResource(R.string.invalid_email))
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Email, stringResource(R.string.icon_email)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = password.isNotEmpty() && !passwordsMatches(password, password2),
                    supportingText = {
                        if (password2.isNotEmpty() && !passwordsMatches(password, password2)) {
                            Text(stringResource(R.string.passwords_not_match))
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, stringResource(R.string.icon_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password2,
                    onValueChange = { password2 = it },
                    label = { Text(stringResource(R.string.confirm_password)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = password2.isNotEmpty() && !passwordsMatches(password, password2),
                    supportingText = {
                        if (password2.isNotEmpty() && !passwordsMatches(password, password2)) {
                            Text(stringResource(R.string.passwords_not_match))
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, stringResource(R.string.icon_confirm_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                Button(
                    onClick = {
                        isLoading = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isEmailValid(email) && passwordsMatches(password, password2) &&
                            password.isNotEmpty() && password2.isNotEmpty() && !isLoading,
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
                        Text(stringResource(R.string.create_account))
                    }
                }

                TextButton(
                    onClick = { navController.navigate("login") },
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
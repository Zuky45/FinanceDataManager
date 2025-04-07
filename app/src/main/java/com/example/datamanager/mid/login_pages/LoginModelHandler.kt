package com.example.datamanager.mid.login_pages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginModelHandler : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun login() {
        _isLoading.value = true
        // In a real implementation, you would call an authentication service here
        // For now, we'll just simulate success after a delay
        _loginSuccess.value = isEmailValid(_email.value)
        _isLoading.value = false
    }

    fun isValidForm(): Boolean {
        return isEmailValid(_email.value) && _password.value.isNotEmpty()
    }

    // Check if email format is valid
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate user credentials
    fun validateCredentials(email: String, password: String): Boolean {
        // For example purposes, you might want to replace this with actual authentication logic
        return email.isNotEmpty() && password.isNotEmpty() && isEmailValid(email)
    }

    // Check if form has valid data to enable submit button
    fun isFormValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && isEmailValid(email)
    }

    fun resetState() {
        _isLoading.value = false
        _loginSuccess.value = false
    }
}
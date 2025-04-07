package com.example.datamanager.mid.login_pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewAccountModelHandler : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _passwordVisibility = MutableStateFlow(false)
    val passwordVisibility = _passwordVisibility.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _accountCreated = MutableStateFlow(false)
    val accountCreated = _accountCreated.asStateFlow()

    private val _accountError = MutableStateFlow(false)
    val accountError = _accountError.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newPassword: String) {
        _confirmPassword.value = newPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisibility.value = !_passwordVisibility.value
    }

    fun resetState() {
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _passwordVisibility.value = false
        _isLoading.value = false
        _accountCreated.value = false
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun isFormValid(email: String, password: String, confirmPassword: String): Boolean {
        return isEmailValid(email) && passwordsMatch(password, confirmPassword) &&
                password.isNotEmpty() && confirmPassword.isNotEmpty()
    }

    fun createAccount() {
        if (!isFormValid(email.value, password.value, confirmPassword.value)) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            delay(2000) // Simulate network request
            _isLoading.value = false
            _accountCreated.value = true
            _accountError.value = false
        }
    }
}
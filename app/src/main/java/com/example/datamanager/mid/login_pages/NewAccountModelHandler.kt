package com.example.datamanager.mid.login_pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.db_manager.DBManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewAccountModelHandler(
    private val dbManager: DBManager = DBManager()
) : ViewModel() {
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

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
        _accountError.value = false
        _errorMessage.value = null
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

    fun passwordValid(password: String): Boolean {
        return password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }
    private fun handleSuccessfulCreation() {
        _isLoading.value = false
        _accountCreated.value = true
        _password.value = ""
        _confirmPassword.value = ""
    }


    fun createAccount() {
        viewModelScope.launch {
            if (!isFormValid(email.value, password.value, confirmPassword.value)) {
                _errorMessage.value = "Please check your form inputs"
                _accountError.value = true
                return@launch
            }

            _isLoading.value = true
            _accountError.value = false
            _accountCreated.value = false

            try {
                val result = dbManager.registerUser(email.value, password.value)
                println(result)

                result.fold(
                    onSuccess = {
                        handleSuccessfulCreation()
                    },
                    onFailure = { error ->
                        _accountError.value = true
                        _errorMessage.value = error.message ?: "Account creation failed. Please try again."
                    }
                )
            } catch (e: Exception) {
                _accountError.value = true
                _errorMessage.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

}
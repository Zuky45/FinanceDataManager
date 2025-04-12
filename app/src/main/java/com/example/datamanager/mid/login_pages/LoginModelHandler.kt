package com.example.datamanager.mid.login_pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.db_manager.DBManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginModelHandler : ViewModel() {
    private val manager = DBManager()
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private var _invalidUser = MutableStateFlow(false)
    var invalidUser: StateFlow<Boolean> = _invalidUser.asStateFlow()

    fun updateEmail(email: String) {
        _email.value = email
        _invalidUser.value = false
    }

    fun updatePassword(password: String) {
        _password.value = password
        _invalidUser.value = false
    }

    fun login() {

        validateUser()

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

    private fun validateUser() {
        _isLoading.value = true
        viewModelScope.launch {
            val response = manager.loginUser(email.value, password.value)
            if (response.isSuccess) {
                _loginSuccess.value = true
                _invalidUser.value = false
                _isLoading.value = false
            } else {
                _loginSuccess.value = false
                _invalidUser.value = true
                _isLoading.value = false
            }

        }




    }
}
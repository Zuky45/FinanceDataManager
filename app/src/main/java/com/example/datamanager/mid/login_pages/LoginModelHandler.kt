package com.example.datamanager.mid.login_pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.db_manager.DBManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel class responsible for handling the login logic and managing the state of the login screen.
 */
class LoginModelHandler : ViewModel() {
    private val manager = DBManager()

    // StateFlow to hold the email input value
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // StateFlow to hold the password input value
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // StateFlow to indicate whether a login operation is in progress
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow to indicate whether the login was successful
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    // StateFlow to indicate whether the user credentials are invalid
    private var _invalidUser = MutableStateFlow(false)
    var invalidUser: StateFlow<Boolean> = _invalidUser.asStateFlow()

    /**
     * Updates the email input value and resets the invalid user state.
     *
     * @param email The new email value.
     */
    fun updateEmail(email: String) {
        _email.value = email
        _invalidUser.value = false
    }

    /**
     * Updates the password input value and resets the invalid user state.
     *
     * @param password The new password value.
     */
    fun updatePassword(password: String) {
        _password.value = password
        _invalidUser.value = false
    }

    /**
     * Initiates the login process by validating the user credentials.
     */
    fun login() {
        validateUser()
    }

    /**
     * Checks if the form is valid by ensuring the email and password are not empty
     * and the email format is valid.
     *
     * @return True if the form is valid, false otherwise.
     */
    fun isValidForm(): Boolean {
        return isEmailValid(_email.value) && _password.value.isNotEmpty()
    }

    /**
     * Validates the format of the provided email.
     *
     * @param email The email to validate.
     * @return True if the email format is valid, false otherwise.
     */
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validates the provided email and password.
     *
     * @param email The email to validate.
     * @param password The password to validate.
     * @return True if both email and password are valid, false otherwise.
     */
    fun validateCredentials(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && isEmailValid(email)
    }

    /**
     * Checks if the form has valid data to enable the submit button.
     *
     * @param email The email input value.
     * @param password The password input value.
     * @return True if the form is valid, false otherwise.
     */
    fun isFormValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && isEmailValid(email)
    }

    /**
     * Resets the state of the ViewModel, including loading and login success indicators.
     */
    fun resetState() {
        _isLoading.value = false
        _loginSuccess.value = false
    }

    /**
     * Validates the user credentials by interacting with the `DBManager` to perform the login operation.
     * Updates the state based on the result of the login attempt.
     */
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
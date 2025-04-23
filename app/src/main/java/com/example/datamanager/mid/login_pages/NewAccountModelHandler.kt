package com.example.datamanager.mid.login_pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.backend.db_manager.auth.DBManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel class responsible for handling the logic and state management for the "Create New Account" screen.
 *
 * @property dbManager The database manager used for account creation operations.
 */
class NewAccountModelHandler(
    private val dbManager: DBManager = DBManager.getInstance()
) : ViewModel() {

    // StateFlow to hold the email input value
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    // StateFlow to hold the password input value
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // StateFlow to hold the confirm password input value
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    // StateFlow to manage the visibility of the password
    private val _passwordVisibility = MutableStateFlow(false)
    val passwordVisibility = _passwordVisibility.asStateFlow()

    // StateFlow to indicate whether an account creation operation is in progress
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // StateFlow to indicate whether the account was successfully created
    private val _accountCreated = MutableStateFlow(false)
    val accountCreated = _accountCreated.asStateFlow()

    // StateFlow to indicate whether there was an error during account creation
    private val _accountError = MutableStateFlow(false)
    val accountError = _accountError.asStateFlow()

    // StateFlow to hold error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    /**
     * Updates the email input value.
     *
     * @param newEmail The new email value.
     */
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    /**
     * Updates the password input value.
     *
     * @param newPassword The new password value.
     */
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Updates the confirm password input value.
     *
     * @param newPassword The new confirm password value.
     */
    fun updateConfirmPassword(newPassword: String) {
        _confirmPassword.value = newPassword
    }

    /**
     * Toggles the visibility of the password.
     */
    fun togglePasswordVisibility() {
        _passwordVisibility.value = !_passwordVisibility.value
    }

    /**
     * Resets the state of the ViewModel, clearing all input values and resetting flags.
     */
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
     * Checks if the password and confirm password match.
     *
     * @param password The password value.
     * @param confirmPassword The confirm password value.
     * @return True if the passwords match, false otherwise.
     */
    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    /**
     * Validates the form inputs for account creation.
     *
     * @param email The email input value.
     * @param password The password input value.
     * @param confirmPassword The confirm password input value.
     * @return True if the form inputs are valid, false otherwise.
     */
    fun isFormValid(email: String, password: String, confirmPassword: String): Boolean {
        return isEmailValid(email) && passwordsMatch(password, confirmPassword) &&
                password.isNotEmpty() && confirmPassword.isNotEmpty()
    }

    /**
     * Validates the password based on length and character requirements.
     *
     * @param password The password to validate.
     * @return True if the password is valid, false otherwise.
     */
    fun passwordValid(password: String): Boolean {
        return password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }

    /**
     * Handles the successful creation of an account by updating the state.
     */
    private fun handleSuccessfulCreation() {
        _isLoading.value = false
        _accountCreated.value = true
        _password.value = ""
        _confirmPassword.value = ""
    }

    /**
     * Initiates the account creation process by validating the form inputs and interacting with the database manager.
     * Updates the state based on the result of the account creation attempt.
     */
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
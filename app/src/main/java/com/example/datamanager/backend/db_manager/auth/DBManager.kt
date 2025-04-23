package com.example.datamanager.backend.db_manager.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class manages the database operations for user authentication and session management.
 */
class DBManager private constructor(){
    private val auth = FirebaseAuth.getInstance()

    /**
     * Registers a new user with the provided email and password.
     *
     * @param email The email address of the user to register.
     * @param password The password for the new user.
     * @return A [Result] containing the user ID on success or an exception on failure.
     */
    suspend fun registerUser(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return@withContext Result.failure(Exception("User registration failed"))

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in a user with the provided email and password.
     *
     * @param email The email address of the user to log in.
     * @param password The password for the user.
     * @return A [Result] containing the user ID on success or an exception on failure.
     */
    suspend fun loginUser(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return@withContext Result.failure(Exception("Login failed"))
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The user ID of the currently logged-in user, or `null` if no user is logged in.
     */
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Logs out the currently logged-in user.
     */
    fun signOut() = auth.signOut()

    companion object {
        @Volatile
        private var instance: DBManager? = null

        fun getInstance(): DBManager {
            return instance ?: synchronized(this) {
                instance ?: DBManager().also { instance = it }
            }
        }
    }

}
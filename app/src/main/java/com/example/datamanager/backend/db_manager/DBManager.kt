package com.example.datamanager.backend.db_manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DBManager {
    private val auth = FirebaseAuth.getInstance()

    // User authentication
    suspend fun registerUser(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return@withContext Result.failure(Exception("User registration failed"))

            // Removed Firestore operations
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return@withContext Result.failure(Exception("Login failed"))
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Session management
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun signOut() = auth.signOut()

    // Note: User settings methods removed as they require Firestore
}
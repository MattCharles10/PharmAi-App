package com.example.pharmai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pharmai.domain.model.User

class AuthViewModel : ViewModel() {

    fun login(email: String, password: String): Result<User> {
        return try {
            val user = User(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                fullName = "User ${email.substringBefore("@")}"
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun register(email: String, password: String, fullName: String): Result<User> {
        return try {
            val user = User(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                fullName = fullName
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        // Simple logout implementation
    }
}
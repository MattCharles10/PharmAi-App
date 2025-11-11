package com.example.pharmai.data.repository

import com.example.pharmai.data.local.DataStoreManager
import com.example.pharmai.data.local.database.dao.UserDao
import com.example.pharmai.data.remote.api.AuthApi
import com.example.pharmai.domain.model.AuthState
import com.example.pharmai.domain.model.User
import com.example.pharmai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userDao: UserDao,
    private val dataStoreManager: DataStoreManager
) : AuthRepository {

    override val authState: Flow<AuthState> = dataStoreManager.isLoggedIn.map { isLoggedIn ->
        if (isLoggedIn) {
            val user = getCurrentUser()
            if (user != null) {
                AuthState.Authenticated(user)
            } else {
                AuthState.Unauthenticated
            }
        } else {
            AuthState.Unauthenticated
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Simulate API call - replace with actual API
            val user = User(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                fullName = "User ${email.substringBefore("@")}"
            )

            // Save to local storage
            dataStoreManager.saveUserSession(user.id, user.email, user.fullName)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, fullName: String): Result<User> {
        return try {
            // Simulate API call - replace with actual API
            val user = User(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                fullName = fullName
            )

            // Save to local storage
            dataStoreManager.saveUserSession(user.id, user.email, user.fullName)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        dataStoreManager.clearUserSession()
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            // Get the first value from the flows
            val isLoggedIn = dataStoreManager.isLoggedIn.first()

            if (isLoggedIn) {
                val userId = dataStoreManager.userId.first() ?: "user_123"
                val userEmail = dataStoreManager.userEmail.first() ?: "user@example.com"
                val userName = dataStoreManager.userName.first() ?: "Demo User"

                User(
                    id = userId,
                    email = userEmail,
                    fullName = userName
                )
            } else {
                null
            }
        } catch (e: Exception) {
            // Fallback to mock user if there's an error
            User(
                id = "user_123",
                email = "user@example.com",
                fullName = "Demo User"
            )
        }
    }
}
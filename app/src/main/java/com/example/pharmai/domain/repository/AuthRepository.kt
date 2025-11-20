package com.example.pharmai.domain.repository

import com.example.pharmai.domain.model.User
import com.example.pharmai.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String): Result<User>
    suspend fun logout(): Result<Unit>
    fun getAuthState(): Flow<AuthState>
    suspend fun getCurrentUser(): User?
}
package com.example.pharmai.domain.repository

import com.example.pharmai.domain.model.AuthState
import com.example.pharmai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, fullName: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
}
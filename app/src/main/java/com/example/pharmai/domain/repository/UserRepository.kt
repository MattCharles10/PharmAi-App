package com.example.pharmai.domain.repository

import com.example.pharmai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): User?
    suspend fun updateUserProfile(user: User)
    suspend fun clearUserData()
}
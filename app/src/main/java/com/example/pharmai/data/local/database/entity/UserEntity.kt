package com.example.pharmai.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val fullName: String,
    val profilePicture: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
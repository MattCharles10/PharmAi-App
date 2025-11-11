package com.example.pharmai.domain.model



data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val profilePicture: String? = null,
    val medications: List<Medication> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
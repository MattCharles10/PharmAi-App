package com.example.pharmai.domain.model


data class User(
    val id: String,
    val email: String,
    val name: String,
    val profilePicture: String? = null
)
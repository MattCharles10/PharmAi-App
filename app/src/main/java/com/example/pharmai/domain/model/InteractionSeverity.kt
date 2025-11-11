package com.example.pharmai.domain.model

enum class InteractionSeverity {
    HIGH, MEDIUM, LOW, NONE;

    fun displayName(): String {
        return when (this) {
            HIGH -> "High Risk"
            MEDIUM -> "Medium Risk"
            LOW -> "Low Risk"
            NONE -> "No Interaction"
        }
    }

    fun color(): androidx.compose.ui.graphics.Color {
        return when (this) {
            HIGH -> androidx.compose.ui.graphics.Color(0xFFD32F2F) // Red
            MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange
            LOW -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green
            NONE -> androidx.compose.ui.graphics.Color(0xFF757575) // Gray
        }
    }
}
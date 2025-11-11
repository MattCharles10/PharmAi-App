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
}
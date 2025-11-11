package com.example.pharmai.domain.model

data class DrugInteraction(
    val medication1: String = "",
    val medication2: String = "",
    val severity: InteractionSeverity = InteractionSeverity.NONE,
    val description: String = "",
    val recommendations: List<String> = emptyList(),
    val riskFactors: List<String> = emptyList()
)
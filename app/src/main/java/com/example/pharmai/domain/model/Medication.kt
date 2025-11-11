package com.example.pharmai.domain.model

data class Medication(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val strength: String = "",
    val type: String = "",
    val description: String = "",
    val interactions: List<DrugInteraction> = emptyList(),
    val imageUrl: String? = null
)
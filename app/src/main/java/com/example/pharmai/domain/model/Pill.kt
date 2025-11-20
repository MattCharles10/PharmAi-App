
package com.example.pharmai.domain.model

data class Pill(
    val id: String,
    val name: String,
    val imprint: String,
    val color: String,
    val shape: String,
    val strength: String,
    val manufacturer: String,
    val description: String,
    val uses: String,
    val warnings: String,
    val confidence: Float = 0.0f
)

data class PillSearchCriteria(
    val imprint: String = "",
    val color: String = "",
    val shape: String = ""
)

data class PillIdentificationState(
    val identifiedPills: List<Pill> = emptyList(),
    val isLoading: Boolean = false,
    val isCameraActive: Boolean = false,
    val cameraPermissionGranted: Boolean = false,
    val error: String? = null
)
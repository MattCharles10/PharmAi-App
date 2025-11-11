package com.example.pharmai.domain.model

import java.time.LocalTime

data class MedicationReminder(
    val id: String,
    val medicationId: String,
    val medicationName: String,
    val dosage: String,
    val time: LocalTime,
    val days: List<Int>, // 1-7 for days of week
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val hour: Int,
    val minute: Int
)
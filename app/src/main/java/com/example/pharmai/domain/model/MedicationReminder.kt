package com.example.pharmai.domain.model

import java.time.LocalTime
import java.util.UUID

data class MedicationReminder(
    val id: String = UUID.randomUUID().toString(),
    val medicationName: String,
    val dosage: String,
    val time: LocalTime,
    val daysOfWeek: List<DayOfWeek>,
    val isActive: Boolean = true,
    val notes: String = ""
) {
    fun getNextReminderTime(): String {
        return time.toString()
    }
}

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    companion object {
        fun fromString(value: String): DayOfWeek {
            return valueOf(value.uppercase())
        }
    }
}

data class ReminderState(
    val reminders: List<MedicationReminder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
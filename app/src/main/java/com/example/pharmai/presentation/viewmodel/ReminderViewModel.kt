package com.example.pharmai.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmai.domain.model.DayOfWeek
import com.example.pharmai.domain.model.MedicationReminder
import com.example.pharmai.domain.model.ReminderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class ReminderViewModel : ViewModel() {

    private val _reminderState = mutableStateOf(ReminderState())
    val reminderState: State<ReminderState> = _reminderState

    private val _showAddReminderDialog = mutableStateOf(false)
    val showAddReminderDialog: State<Boolean> = _showAddReminderDialog

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            _reminderState.value = _reminderState.value.copy(isLoading = true)
            // Simulate loading from database
            kotlinx.coroutines.delay(500)

            // Mock data for demo
            val mockReminders = listOf(
                MedicationReminder(
                    medicationName = "Aspirin",
                    dosage = "81mg",
                    time = LocalTime.of(8, 0),
                    daysOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                    notes = "Take with food"
                ),
                MedicationReminder(
                    medicationName = "Metformin",
                    dosage = "500mg",
                    time = LocalTime.of(12, 0),
                    daysOfWeek = DayOfWeek.values().toList(),
                    notes = "With breakfast and dinner"
                )
            )

            _reminderState.value = ReminderState(reminders = mockReminders)
        }
    }

    fun addReminder(reminder: MedicationReminder) {
        viewModelScope.launch {
            val currentReminders = _reminderState.value.reminders.toMutableList()
            currentReminders.add(reminder)
            _reminderState.value = _reminderState.value.copy(reminders = currentReminders)
            _showAddReminderDialog.value = false
        }
    }

    fun updateReminder(reminder: MedicationReminder) {
        viewModelScope.launch {
            val currentReminders = _reminderState.value.reminders.toMutableList()
            val index = currentReminders.indexOfFirst { it.id == reminder.id }
            if (index != -1) {
                currentReminders[index] = reminder
                _reminderState.value = _reminderState.value.copy(reminders = currentReminders)
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            val currentReminders = _reminderState.value.reminders.toMutableList()
            currentReminders.removeAll { it.id == reminderId }
            _reminderState.value = _reminderState.value.copy(reminders = currentReminders)
        }
    }

    fun toggleReminderActive(reminderId: String) {
        viewModelScope.launch {
            val currentReminders = _reminderState.value.reminders.toMutableList()
            val index = currentReminders.indexOfFirst { it.id == reminderId }
            if (index != -1) {
                val reminder = currentReminders[index]
                currentReminders[index] = reminder.copy(isActive = !reminder.isActive)
                _reminderState.value = _reminderState.value.copy(reminders = currentReminders)
            }
        }
    }

    fun showAddReminderDialog() {
        _showAddReminderDialog.value = true
    }

    fun hideAddReminderDialog() {
        _showAddReminderDialog.value = false
    }
}
package com.example.pharmai.presentation.component.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pharmai.domain.model.DayOfWeek
import com.example.pharmai.domain.model.MedicationReminder
import java.time.LocalTime

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (MedicationReminder) -> Unit,
    existingReminder: MedicationReminder? = null
) {
    var medicationName by remember { mutableStateOf(existingReminder?.medicationName ?: "") }
    var dosage by remember { mutableStateOf(existingReminder?.dosage ?: "") }
    var selectedTime by remember {
        mutableStateOf(existingReminder?.time ?: LocalTime.of(8, 0))
    }
    var selectedDays by remember {
        mutableStateOf(existingReminder?.daysOfWeek?.toSet() ?: emptySet())
    }
    var notes by remember { mutableStateOf(existingReminder?.notes ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingReminder != null) "Edit Reminder" else "Add Reminder",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medication Name
                OutlinedTextField(
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dosage
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Time Picker
                Text(
                    text = "Time: ${selectedTime.hour}:${selectedTime.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimePickerSection(
                    selectedTime = selectedTime,
                    onTimeChange = { selectedTime = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Days of Week
                Text(
                    text = "Repeat on:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                DaysOfWeekSelector(
                    selectedDays = selectedDays,
                    onDaysChange = { selectedDays = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        if (medicationName.isNotBlank() && dosage.isNotBlank()) {
                            val reminder = MedicationReminder(
                                id = existingReminder?.id ?: "",
                                medicationName = medicationName,
                                dosage = dosage,
                                time = selectedTime,
                                daysOfWeek = selectedDays.toList(),
                                notes = notes
                            )
                            onSave(reminder)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = medicationName.isNotBlank() && dosage.isNotBlank()
                ) {
                    Text(if (existingReminder != null) "Update Reminder" else "Add Reminder")
                }
            }
        }
    }
}

@Composable
fun TimePickerSection(
    selectedTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    // Display current selected time
    OutlinedCard(
        onClick = { showTimePicker = true },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Reminder Time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${selectedTime.hour}:${selectedTime.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Select time",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text(
                    "Select Time",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                SimpleTimePicker(
                    selectedTime = selectedTime,
                    onTimeChange = onTimeChange
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showTimePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("DONE")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun SimpleTimePicker(
    selectedTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    var hours by remember { mutableStateOf(selectedTime.hour) }
    var minutes by remember { mutableStateOf(selectedTime.minute) }

    // Update parent when time changes
    LaunchedEffect(hours, minutes) {
        onTimeChange(LocalTime.of(hours, minutes))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Time Display
        Text(
            text = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time Selection Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hours Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HOURS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hours Increase Button
                IconButton(
                    onClick = {
                        hours = (hours + 1) % 24
                        onTimeChange(LocalTime.of(hours, minutes))
                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Increase hours",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = hours.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Hours Decrease Button
                IconButton(
                    onClick = {
                        hours = if (hours == 0) 23 else hours - 1
                        onTimeChange(LocalTime.of(hours, minutes))
                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Decrease hours",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Separator
            Text(
                text = ":",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Minutes Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MINUTES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Minutes Increase Button
                IconButton(
                    onClick = {
                        minutes = (minutes + 5) % 60
                        onTimeChange(LocalTime.of(hours, minutes))
                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Increase minutes",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = minutes.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Minutes Decrease Button
                IconButton(
                    onClick = {
                        minutes = if (minutes == 0) 55 else minutes - 5
                        onTimeChange(LocalTime.of(hours, minutes))
                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Decrease minutes",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Quick Time Presets
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Presets",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                LocalTime.of(8, 0) to "AM",
                LocalTime.of(12, 0) to "Noon",
                LocalTime.of(18, 0) to "PM",
                LocalTime.of(20, 0) to "Night"
            ).forEach { (time, label) ->
                FilterChip(
                    selected = hours == time.hour && minutes == time.minute,
                    onClick = {
                        hours = time.hour
                        minutes = time.minute
                        onTimeChange(LocalTime.of(hours, minutes))
                    },
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
fun DaysOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDaysChange: (Set<DayOfWeek>) -> Unit
) {
    val days = DayOfWeek.values()

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { day ->
            val isSelected = selectedDays.contains(day)
            FilterChip(
                selected = isSelected,
                onClick = {
                    val newSelection = selectedDays.toMutableSet()
                    if (isSelected) {
                        newSelection.remove(day)
                    } else {
                        newSelection.add(day)
                    }
                    onDaysChange(newSelection)
                },
                label = { Text(day.name.take(3)) }
            )
        }
    }
}

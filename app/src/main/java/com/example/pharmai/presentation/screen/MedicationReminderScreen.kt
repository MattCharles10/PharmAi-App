package com.example.pharmai.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmai.presentation.component.reminder.AddReminderDialog
import com.example.pharmai.presentation.component.reminder.ReminderCard
import com.example.pharmai.presentation.viewmodel.ReminderViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationReminderScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: ReminderViewModel = viewModel()
    val reminderState = viewModel.reminderState.value
    val showAddDialog = viewModel.showAddReminderDialog.value

    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        contentVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Medication Reminders")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddReminderDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Reminder Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Active: ${reminderState.reminders.count { it.isActive }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total: ${reminderState.reminders.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminders List
                if (reminderState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading reminders...")
                        }
                    }
                } else if (reminderState.reminders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "No reminders",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No reminders yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap the + button to add your first reminder",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reminderState.reminders) { reminder ->
                            ReminderCard(
                                reminder = reminder,
                                onEdit = {
                                    // For now, just show add dialog with existing data
                                    // In a real app, you'd have an edit mode
                                    viewModel.showAddReminderDialog()
                                },
                                onDelete = {
                                    viewModel.deleteReminder(reminder.id)
                                },
                                onToggleActive = {
                                    viewModel.toggleReminderActive(reminder.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Add Reminder Dialog
        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { viewModel.hideAddReminderDialog() },
                onSave = { reminder ->
                    viewModel.addReminder(reminder)
                }
            )
        }
    }
}
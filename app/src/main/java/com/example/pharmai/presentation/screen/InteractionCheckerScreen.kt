package com.example.pharmai.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmai.domain.model.InteractionSeverity
import com.example.pharmai.presentation.viewmodel.MedicationViewModel

@Composable
fun InteractionCheckerScreen(
    onBackClick: () -> Unit
) {
    val viewModel: MedicationViewModel = viewModel()
    var medication1 by remember { mutableStateOf("") }
    var medication2 by remember { mutableStateOf("") }
    val interactionResult by viewModel.interactionResult.collectAsState()
    val isCheckingInteractions by viewModel.isCheckingInteractions.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                println("DEBUG: Back button clicked in InteractionChecker")
                onBackClick()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Interaction Checker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = "Check potential interactions between two medications",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Medication Inputs
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = medication1,
                onValueChange = { medication1 = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("First Medication") },
                placeholder = { Text("e.g., Aspirin") },
                leadingIcon = { Icon(Icons.Default.Medication, contentDescription = "Medication 1") },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = medication2,
                onValueChange = { medication2 = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Second Medication") },
                placeholder = { Text("e.g., Ibuprofen") },
                leadingIcon = { Icon(Icons.Default.Medication, contentDescription = "Medication 2") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (medication1.isNotEmpty() && medication2.isNotEmpty()) {
                            viewModel.checkInteractions(medication1, medication2)
                        }
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Check Button
        Button(
            onClick = {
                keyboardController?.hide()
                viewModel.checkInteractions(medication1, medication2)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = medication1.isNotEmpty() && medication2.isNotEmpty() && !isCheckingInteractions,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isCheckingInteractions) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text("Check Interactions")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Loading
        AnimatedVisibility(visible = isCheckingInteractions) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analyzing interactions...")
                }
            }
        }

        // Results
        AnimatedVisibility(visible = interactionResult != null && !isCheckingInteractions) {
            interactionResult?.let { result ->
                InteractionResultCard(result = result)
            }
        }

        // Empty State
        AnimatedVisibility(visible = medication1.isEmpty() || medication2.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Enter medications",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enter two medications to check interactions",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun InteractionResultCard(result: com.example.pharmai.domain.model.DrugInteraction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (result.severity) {
                InteractionSeverity.HIGH -> MaterialTheme.colorScheme.errorContainer
                InteractionSeverity.MEDIUM -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                InteractionSeverity.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                InteractionSeverity.NONE -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Severity
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = when (result.severity) {
                        InteractionSeverity.HIGH -> Icons.Default.Dangerous
                        InteractionSeverity.MEDIUM -> Icons.Default.Warning
                        InteractionSeverity.LOW -> Icons.Default.Info
                        InteractionSeverity.NONE -> Icons.Default.CheckCircle
                    },
                    contentDescription = "Severity",
                    tint = when (result.severity) {
                        InteractionSeverity.HIGH -> MaterialTheme.colorScheme.error
                        InteractionSeverity.MEDIUM -> MaterialTheme.colorScheme.error
                        InteractionSeverity.LOW -> MaterialTheme.colorScheme.primary
                        InteractionSeverity.NONE -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = result.severity.displayName(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (result.severity) {
                        InteractionSeverity.HIGH -> MaterialTheme.colorScheme.error
                        InteractionSeverity.MEDIUM -> MaterialTheme.colorScheme.error
                        InteractionSeverity.LOW -> MaterialTheme.colorScheme.onSurface
                        InteractionSeverity.NONE -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "${result.medication1} + ${result.medication2}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = result.description, style = MaterialTheme.typography.bodyMedium)

            if (result.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Recommendations:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                result.recommendations.forEach { recommendation ->
                    Text(text = "• $recommendation", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
    }
}
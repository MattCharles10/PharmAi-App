
package com.example.pharmai.presentation.component.medication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pharmai.domain.model.Medication
import com.example.pharmai.domain.model.DrugInteraction
import com.example.pharmai.domain.model.InteractionSeverity

@Composable
fun MedicationCard(medication: Medication) {
    var showInteractions by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = "Medication",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (medication.strength.isNotEmpty()) {
                    Text(
                        text = medication.strength,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            if (medication.description.isNotEmpty()) {
                Text(
                    text = medication.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Dosage and Type Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (medication.dosage.isNotEmpty()) {
                    MedicationChip(text = medication.dosage)
                }
                if (medication.type.isNotEmpty()) {
                    MedicationChip(text = medication.type)
                }

                // Source indicator
                MedicationChip(
                    text = medication.source,
                    color = when (medication.source) {
                        "RxNorm API" -> MaterialTheme.colorScheme.primary
                        "FDA" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }

            // Interactions Section
            if (medication.interactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                // Interactions Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Drug Interactions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Interaction count with highest severity
                    val highestSeverity = medication.interactions.maxByOrNull { it.severity }?.severity ?: InteractionSeverity.NONE
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${medication.interactions.size} interaction(s)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SeverityChip(severity = highestSeverity)
                    }
                }

                // Expandable Interactions
                AnimatedVisibility(visible = showInteractions) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        medication.interactions.forEach { interaction ->
                            InteractionItem(interaction = interaction)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // Toggle Button
                TextButton(
                    onClick = { showInteractions = !showInteractions },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = if (showInteractions) "Hide Interactions" else "Show Interactions",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Icon(
                        imageVector = if (showInteractions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showInteractions) "Hide" else "Show",
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                // No interactions message
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No known drug interactions",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InteractionItem(interaction: DrugInteraction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Interaction Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeverityChip(severity = interaction.severity)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${interaction.medication1} + ${interaction.medication2}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = interaction.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Recommendations
            if (interaction.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recommendations:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                interaction.recommendations.forEach { recommendation ->
                    Text(
                        text = "• $recommendation",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Risk Factors
            if (interaction.riskFactors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Risk Factors:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                interaction.riskFactors.forEach { riskFactor ->
                    Text(
                        text = "• $riskFactor",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SeverityChip(severity: InteractionSeverity) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)),
        color = severity.color().copy(alpha = 0.1f)
    ) {
        Text(
            text = severity.displayName(),
            style = MaterialTheme.typography.labelSmall,
            color = severity.color(),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun MedicationChip(
    text: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
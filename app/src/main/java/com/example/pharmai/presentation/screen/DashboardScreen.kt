package com.example.pharmai.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmai.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToMedicationSearch: () -> Unit = {},
    onNavigateToInteractionChecker: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToPillIdentification: () -> Unit = {}
) {
    val viewModel: AuthViewModel = viewModel()
    val currentUser = viewModel.currentUser.value
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        contentVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize()
    ) {
        // Animated Header
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome,",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currentUser?.name ?: "User",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Logout")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Animated Features Grid
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 400))
        ) {
            Column {
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Medication Search Card
                    FeatureCard(
                        title = "Medication Search",
                        description = "Search for medications and drug information",
                        icon = Icons.Default.Search,
                        onClick = onNavigateToMedicationSearch,
                        delay = 0
                    )

                    // Interaction Checker Card
                    FeatureCard(
                        title = "Interaction Checker",
                        description = "Check for drug interactions",
                        icon = Icons.Default.Warning,
                        onClick = onNavigateToInteractionChecker,
                        delay = 100
                    )

                    // Medication Reminder Card
                    FeatureCard(
                        title = "Medication Reminder",
                        description = "Set reminders for your medications",
                        icon = Icons.Default.Schedule,
                        onClick = onNavigateToReminders,
                        delay = 200
                    )

                    // Pill Identification Card
                    FeatureCard(
                        title = "Pill Identification",
                        description = "Identify medications using camera",
                        icon = Icons.Default.Medication,
                        onClick = onNavigateToPillIdentification,
                        delay = 300
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    delay: Int = 0
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = delay)) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600, delayMillis = delay)
                )
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
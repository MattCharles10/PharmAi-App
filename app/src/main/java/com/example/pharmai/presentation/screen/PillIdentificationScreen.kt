// 📄 presentation/screen/PillIdentificationScreen.kt
package com.example.pharmai.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmai.presentation.component.pill.CameraView
import com.example.pharmai.presentation.component.pill.PillCard
import com.example.pharmai.presentation.component.pill.PillSearchCriteria
import com.example.pharmai.presentation.viewmodel.PillIdentificationViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillIdentificationScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: PillIdentificationViewModel = viewModel()
    val pillState = viewModel.pillState.value
    val searchCriteria = viewModel.searchCriteria.value
    val showCamera = viewModel.showCamera.value

    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        contentVisible = true
    }

    // Show camera if activated
    if (showCamera) {
        CameraView(
            onImageCaptured = { imageData ->
                viewModel.identifyPillFromImage(imageData)
            },
            onClose = {
                viewModel.hideCamera()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Pill Identification")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (pillState.identifiedPills.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearResults() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Results")
                        }
                    }
                }
            )
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
                // Search Methods Card
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
                            text = "Identify Pills",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Use camera to identify pills or search by imprint, color, and shape",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Camera Button
                        Button(
                            onClick = { viewModel.showCamera() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Identify with Camera")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Criteria
                PillSearchCriteria(
                    criteria = searchCriteria,
                    onCriteriaChange = { viewModel.updateSearchCriteria(it) },
                    onSearch = { viewModel.searchPillsByCriteria(searchCriteria) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Results Section
                if (pillState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Identifying pill...")
                        }
                    }
                } else if (pillState.identifiedPills.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Identified Pills (${pillState.identifiedPills.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pillState.identifiedPills) { pill ->
                                PillCard(pill = pill)
                            }
                        }
                    }
                } else if (searchCriteria.imprint.isNotBlank() || searchCriteria.color.isNotBlank() || searchCriteria.shape.isNotBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "No results",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No pills found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Try different search criteria",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Get started",
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Identify Your Pills",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Use the camera to identify unknown pills\nor search by imprint, color, and shape",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
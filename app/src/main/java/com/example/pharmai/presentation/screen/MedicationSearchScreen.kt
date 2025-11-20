package com.example.pharmai.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSearchScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<Medication>()) }
    var isLoading by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        contentVisible = true
    }

    // Use LaunchedEffect to handle search
    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 2) {
            isLoading = true
            delay(500) // Simulate network delay
            searchResults = performSearch(searchQuery)
            isLoading = false
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Medication Search")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search medications...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Results or Empty State
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Searching...")
                        }
                    }
                } else if (searchResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { medication ->
                            MedicationCard(medication = medication)
                        }
                    }
                } else if (searchQuery.length > 2) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No medications found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Search for medications by name",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Type at least 3 characters to search",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationCard(medication: Medication) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = medication.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dosage: ${medication.dosage}",
                style = MaterialTheme.typography.bodySmall
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Uses: ${medication.uses}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Side Effects: ${medication.sideEffects}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// Mock function to simulate search
private fun performSearch(query: String): List<Medication> {
    val mockMedications = listOf(
        Medication(
            name = "Aspirin",
            description = "Pain reliever and anti-inflammatory",
            dosage = "81mg",
            uses = "Pain relief, fever reduction, anti-inflammatory",
            sideEffects = "Stomach upset, bleeding risk"
        ),
        Medication(
            name = "Lisinopril",
            description = "ACE inhibitor for blood pressure",
            dosage = "10mg",
            uses = "High blood pressure, heart failure",
            sideEffects = "Cough, dizziness, kidney problems"
        ),
        Medication(
            name = "Metformin",
            description = "Diabetes medication",
            dosage = "500mg",
            uses = "Type 2 diabetes management",
            sideEffects = "Nausea, diarrhea, stomach upset"
        ),
        Medication(
            name = "Atorvastatin",
            description = "Cholesterol medication",
            dosage = "20mg",
            uses = "High cholesterol, heart disease prevention",
            sideEffects = "Muscle pain, liver problems"
        ),
        Medication(
            name = "Levothyroxine",
            description = "Thyroid hormone replacement",
            dosage = "50mcg",
            uses = "Hypothyroidism treatment",
            sideEffects = "Palpitations, weight loss, insomnia"
        ),
        Medication(
            name = "Amoxicillin",
            description = "Antibiotic",
            dosage = "500mg",
            uses = "Bacterial infections",
            sideEffects = "Diarrhea, nausea, rash"
        ),
        Medication(
            name = "Ibuprofen",
            description = "NSAID pain reliever",
            dosage = "400mg",
            uses = "Pain, inflammation, fever",
            sideEffects = "Stomach upset, kidney problems"
        )
    )

    return mockMedications.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
    }
}

data class Medication(
    val name: String,
    val description: String,
    val dosage: String,
    val uses: String = "Not specified",
    val sideEffects: String = "Not specified"
)
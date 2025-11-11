package com.example.pharmai.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmai.domain.model.Medication
import com.example.pharmai.domain.model.DrugInteraction
import com.example.pharmai.domain.model.InteractionSeverity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicationViewModel : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Medication>>(emptyList())
    val searchResults: StateFlow<List<Medication>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _interactionResult = MutableStateFlow<DrugInteraction?>(null)
    val interactionResult: StateFlow<DrugInteraction?> = _interactionResult.asStateFlow()

    private val _isCheckingInteractions = MutableStateFlow(false)
    val isCheckingInteractions: StateFlow<Boolean> = _isCheckingInteractions.asStateFlow()

    private val mockMedications = listOf(
        Medication(
            id = "1", name = "Aspirin", dosage = "81mg", strength = "Low",
            type = "NSAID", description = "Pain reliever and anti-inflammatory"
        ),
        Medication(
            id = "2", name = "Lisinopril", dosage = "10mg", strength = "Medium",
            type = "Antihypertensive", description = "ACE inhibitor for high blood pressure"
        ),
        Medication(
            id = "3", name = "Ibuprofen", dosage = "400mg", strength = "Medium",
            type = "NSAID", description = "Non-steroidal anti-inflammatory drug"
        )
    )

    fun searchMedications(query: String) {
        _isSearching.value = true
        viewModelScope.launch {
            delay(500)
            val results = if (query.isBlank()) emptyList() else {
                mockMedications.filter { it.name.contains(query, ignoreCase = true) }
            }
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun checkInteractions(medication1: String, medication2: String) {
        _isCheckingInteractions.value = true
        viewModelScope.launch {
            delay(1000)
            _interactionResult.value = DrugInteraction(
                medication1 = medication1,
                medication2 = medication2,
                severity = InteractionSeverity.LOW,
                description = "No significant interactions found. Always consult your healthcare provider.",
                recommendations = listOf("Consult your doctor", "Monitor for side effects"),
                riskFactors = emptyList()
            )
            _isCheckingInteractions.value = false
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    fun clearInteractionResult() {
        _interactionResult.value = null
    }
}
package com.example.pharmai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmai.data.repository.MedicationRepositoryImpl
import com.example.pharmai.data.remote.api.RetrofitClient
import com.example.pharmai.domain.model.Medication
import com.example.pharmai.domain.model.DrugInteraction
import com.example.pharmai.domain.model.InteractionSeverity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicationViewModel : ViewModel() {

    private val repository = MedicationRepositoryImpl(RetrofitClient.rxNormApi)

    private val _searchResults = MutableStateFlow<List<Medication>>(emptyList())
    val searchResults: StateFlow<List<Medication>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _interactionResult = MutableStateFlow<DrugInteraction?>(null)
    val interactionResult: StateFlow<DrugInteraction?> = _interactionResult.asStateFlow()

    private val _isCheckingInteractions = MutableStateFlow(false)
    val isCheckingInteractions: StateFlow<Boolean> = _isCheckingInteractions.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    private val _selectedMedication = MutableStateFlow<Medication?>(null)
    val selectedMedication: StateFlow<Medication?> = _selectedMedication.asStateFlow()

    fun searchMedications(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _searchError.value = null
            return
        }

        _isSearching.value = true
        _searchError.value = null

        viewModelScope.launch {
            try {
                println("🔄 DEBUG: Starting API search for: '$query'")

                // Real API call
                val apiResults = repository.searchMedications(query)
                println("✅ DEBUG: Repository returned ${apiResults.size} results")

                if (apiResults.isNotEmpty()) {
                    // Use real API results
                    _searchResults.value = apiResults
                    println("🎯 DEBUG: Showing ${apiResults.size} REAL API results")
                    apiResults.forEach { med ->
                        println("   - ${med.name} (${med.source})")
                    }
                } else {
                    // Fallback to demo data if API returns empty
                    println("⚠️ DEBUG: API returned empty, using demo data")
                    val demoResults = getDemoMedications().filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                    _searchResults.value = demoResults
                    _searchError.value = "No results from API. Showing demo data."
                    println("📋 DEBUG: Showing ${demoResults.size} demo results")
                }

            } catch (e: Exception) {
                println("❌ DEBUG: Search error: ${e.message}")
                _searchError.value = "Search failed: ${e.message}. Using demo data."
                // Fallback to demo data
                val demoResults = getDemoMedications().filter {
                    it.name.contains(query, ignoreCase = true)
                }
                _searchResults.value = demoResults
                println("📋 DEBUG: Error fallback - showing ${demoResults.size} demo results")
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun checkInteractions(medication1: String, medication2: String) {
        _isCheckingInteractions.value = true
        _interactionResult.value = null

        viewModelScope.launch {
            try {
                delay(1000) // Simulate API call
                val interaction = simulateInteractionCheck(medication1, medication2)
                _interactionResult.value = interaction
                println("🔍 DEBUG: Interaction check completed: ${interaction.severity}")
            } catch (e: Exception) {
                println("❌ DEBUG: Interaction check error: ${e.message}")
                _interactionResult.value = DrugInteraction(
                    medication1 = medication1,
                    medication2 = medication2,
                    severity = InteractionSeverity.LOW,
                    description = "Unable to check interactions at this time.",
                    recommendations = listOf("Please try again later"),
                    riskFactors = emptyList()
                )
            } finally {
                _isCheckingInteractions.value = false
            }
        }
    }

    fun selectMedication(medication: Medication) {
        _selectedMedication.value = medication
        println("📝 DEBUG: Selected medication: ${medication.name}")
    }

    fun clearSelectedMedication() {
        _selectedMedication.value = null
        println("📝 DEBUG: Cleared selected medication")
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _searchError.value = null
        println("🗑️ DEBUG: Cleared search results")
    }

    fun clearInteractionResult() {
        _interactionResult.value = null
        println("🗑️ DEBUG: Cleared interaction result")
    }

    fun clearError() {
        _searchError.value = null
        println("🗑️ DEBUG: Cleared error")
    }

    private fun simulateInteractionCheck(med1: String, med2: String): DrugInteraction {
        val interactionKey = "${med1.lowercase()}_${med2.lowercase()}"
        println("🔍 DEBUG: Checking interaction for: $interactionKey")

        return when (interactionKey) {
            "aspirin_warfarin", "warfarin_aspirin" -> DrugInteraction(
                medication1 = med1,
                medication2 = med2,
                severity = InteractionSeverity.HIGH,
                description = "Increased risk of bleeding and bruising. Concurrent use significantly increases the risk of gastrointestinal bleeding and other hemorrhagic complications.",
                recommendations = listOf(
                    "Monitor INR closely (target 2.0-3.0)",
                    "Consider alternative pain relief (acetaminophen)",
                    "Watch for signs of bleeding (bruising, dark stools)",
                    "Regular CBC and stool occult blood tests",
                    "Patient education about bleeding risks"
                ),
                riskFactors = listOf(
                    "Age > 65 years",
                    "History of GI bleeding or ulcers",
                    "Renal impairment",
                    "Liver disease",
                    "Concomitant NSAID use"
                )
            )
            "aspirin_ibuprofen", "ibuprofen_aspirin" -> DrugInteraction(
                medication1 = med1,
                medication2 = med2,
                severity = InteractionSeverity.MEDIUM,
                description = "Ibuprofen may competitively inhibit aspirin's binding to platelets, reducing its antiplatelet and cardioprotective effects. Increased risk of gastrointestinal complications.",
                recommendations = listOf(
                    "Take ibuprofen at least 2 hours after aspirin",
                    "Consider acetaminophen for pain relief instead",
                    "Use gastroprotective agents if chronic NSAID use required",
                    "Monitor for GI symptoms"
                ),
                riskFactors = listOf(
                    "Cardiovascular disease requiring aspirin therapy",
                    "Chronic NSAID use",
                    "History of GI complications",
                    "Elderly patients"
                )
            )
            "lisinopril_potassium", "potassium_lisinopril" -> DrugInteraction(
                medication1 = med1,
                medication2 = med2,
                severity = InteractionSeverity.MEDIUM,
                description = "ACE inhibitors like Lisinopril can cause potassium retention, potentially leading to hyperkalemia when combined with potassium supplements or potassium-sparing diuretics.",
                recommendations = listOf(
                    "Monitor serum potassium levels regularly",
                    "Avoid high-potassium diets and salt substitutes",
                    "Consider alternative antihypertensive if hyperkalemia persists",
                    "Educate patient about potassium-rich foods to avoid"
                ),
                riskFactors = listOf(
                    "Renal impairment",
                    "Diabetes mellitus",
                    "Concomitant use of potassium-sparing diuretics",
                    "Elderly patients"
                )
            )
            "metformin_contrast", "contrast_metformin" -> DrugInteraction(
                medication1 = med1,
                medication2 = med2,
                severity = InteractionSeverity.HIGH,
                description = "Iodinated contrast media can impair renal function, increasing the risk of metformin-associated lactic acidosis due to reduced drug clearance.",
                recommendations = listOf(
                    "Discontinue metformin 48 hours before contrast procedure",
                    "Resume metformin only after confirming normal renal function 48 hours post-procedure",
                    "Monitor renal function and lactate levels",
                    "Ensure adequate hydration before and after procedure"
                ),
                riskFactors = listOf(
                    "Pre-existing renal impairment (eGFR < 60 mL/min)",
                    "Dehydration",
                    "Advanced age",
                    "Congestive heart failure"
                )
            )
            else -> DrugInteraction(
                medication1 = med1,
                medication2 = med2,
                severity = InteractionSeverity.LOW,
                description = "No clinically significant interaction detected between $med1 and $med2 based on available data. However, always monitor for individual patient responses.",
                recommendations = listOf(
                    "Continue monitoring as usual",
                    "Report any unusual symptoms to healthcare provider",
                    "Maintain regular medication schedule"
                ),
                riskFactors = emptyList()
            )
        }
    }

    private fun getDemoMedications(): List<Medication> {
        return listOf(
            Medication(
                id = "demo_1",
                name = "Aspirin",
                dosage = "81 mg once daily",
                strength = "81 mg",
                type = "Antiplatelet",
                description = "Salicylate medication used for pain, fever, inflammation, and antiplatelet effects. Also used for cardiovascular protection in appropriate patients.",
                interactions = getInteractionsForDrug("Aspirin"),
                source = "Demo Data"
            ),
            Medication(
                id = "demo_2",
                name = "Lisinopril",
                dosage = "10 mg once daily",
                strength = "10 mg",
                type = "ACE Inhibitor",
                description = "Angiotensin-converting enzyme inhibitor used for hypertension, heart failure, and post-myocardial infarction management.",
                interactions = getInteractionsForDrug("Lisinopril"),
                source = "Demo Data"
            ),
            Medication(
                id = "demo_3",
                name = "Metformin",
                dosage = "500 mg twice daily",
                strength = "500 mg",
                type = "Biguanide",
                description = "First-line oral antidiabetic medication for type 2 diabetes. Improves insulin sensitivity and reduces hepatic glucose production.",
                interactions = getInteractionsForDrug("Metformin"),
                source = "Demo Data"
            ),
            Medication(
                id = "demo_4",
                name = "Atorvastatin",
                dosage = "20 mg once daily",
                strength = "20 mg",
                type = "Statin",
                description = "HMG-CoA reductase inhibitor used for cholesterol management and cardiovascular risk reduction.",
                interactions = emptyList(),
                source = "Demo Data"
            ),
            Medication(
                id = "demo_5",
                name = "Warfarin",
                dosage = "5 mg once daily",
                strength = "5 mg",
                type = "Anticoagulant",
                description = "Vitamin K antagonist used for prevention and treatment of thromboembolic disorders. Requires regular INR monitoring.",
                interactions = getInteractionsForDrug("Warfarin"),
                source = "Demo Data"
            ),
            Medication(
                id = "demo_6",
                name = "Levothyroxine",
                dosage = "50 mcg once daily",
                strength = "50 mcg",
                type = "Thyroid Hormone",
                description = "Synthetic thyroid hormone replacement therapy for hypothyroidism. Should be taken on empty stomach.",
                interactions = emptyList(),
                source = "Demo Data"
            )
        )
    }

    private fun getInteractionsForDrug(drugName: String): List<DrugInteraction> {
        return when (drugName.lowercase()) {
            "aspirin" -> listOf(
                DrugInteraction(
                    medication1 = "Aspirin",
                    medication2 = "Warfarin",
                    severity = InteractionSeverity.HIGH,
                    description = "Increased risk of bleeding and bruising",
                    recommendations = listOf(
                        "Monitor INR closely",
                        "Consider alternative pain relief",
                        "Watch for signs of bleeding"
                    ),
                    riskFactors = listOf(
                        "Elderly patients",
                        "History of GI bleeding",
                        "Renal impairment"
                    )
                ),
                DrugInteraction(
                    medication1 = "Aspirin",
                    medication2 = "Ibuprofen",
                    severity = InteractionSeverity.MEDIUM,
                    description = "Ibuprofen may reduce aspirin's cardioprotective effects",
                    recommendations = listOf(
                        "Take ibuprofen 2 hours after aspirin",
                        "Consider acetaminophen instead"
                    ),
                    riskFactors = listOf(
                        "Cardiovascular disease",
                        "Chronic NSAID use"
                    )
                )
            )
            "warfarin" -> listOf(
                DrugInteraction(
                    medication1 = "Warfarin",
                    medication2 = "Aspirin",
                    severity = InteractionSeverity.HIGH,
                    description = "Increased anticoagulant effect and bleeding risk",
                    recommendations = listOf(
                        "Avoid concurrent use if possible",
                        "Monitor INR weekly",
                        "Educate patient about bleeding signs"
                    ),
                    riskFactors = listOf(
                        "Age > 65",
                        "Previous bleeding history",
                        "Liver disease"
                    )
                )
            )
            "lisinopril" -> listOf(
                DrugInteraction(
                    medication1 = "Lisinopril",
                    medication2 = "Potassium Supplements",
                    severity = InteractionSeverity.MEDIUM,
                    description = "Risk of hyperkalemia (high potassium levels)",
                    recommendations = listOf(
                        "Monitor potassium levels regularly",
                        "Avoid high-potassium diets",
                        "Consider alternative antihypertensive"
                    ),
                    riskFactors = listOf(
                        "Renal impairment",
                        "Diabetes",
                        "Elderly patients"
                    )
                )
            )
            "metformin" -> listOf(
                DrugInteraction(
                    medication1 = "Metformin",
                    medication2 = "Contrast Dye",
                    severity = InteractionSeverity.HIGH,
                    description = "Risk of lactic acidosis and kidney damage",
                    recommendations = listOf(
                        "Discontinue metformin before contrast procedures",
                        "Resume 48 hours after procedure if renal function normal",
                        "Monitor renal function closely"
                    ),
                    riskFactors = listOf(
                        "Pre-existing renal impairment",
                        "Dehydration",
                        "Advanced age"
                    )
                )
            )
            else -> emptyList()
        }
    }
}
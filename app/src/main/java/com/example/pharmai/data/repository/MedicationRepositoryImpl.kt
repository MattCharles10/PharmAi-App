package com.example.pharmai.data.repository

import com.example.pharmai.data.remote.api.ConceptProperty
import com.example.pharmai.data.remote.api.RxNormApi
import com.example.pharmai.data.remote.api.RxNormDrugResponse
import com.example.pharmai.domain.model.Medication
import com.example.pharmai.domain.model.DrugInteraction
import com.example.pharmai.domain.model.InteractionSeverity

class MedicationRepositoryImpl(
    private val rxNormApi: RxNormApi
) {
    suspend fun searchMedications(query: String): List<Medication> {
        return try {
            println("DEBUG: Making API call for: $query")
            val response = rxNormApi.searchDrugs(query)
            println("DEBUG: API Response: $response")

            val medications = mapRxNormResponseToMedications(response)
            println("DEBUG: Mapped ${medications.size} medications")

            medications
        } catch (e: Exception) {
            println("DEBUG: API Error: ${e.message}")
            emptyList()
        }
    }

    private fun mapRxNormResponseToMedications(response: RxNormDrugResponse): List<Medication> {
        val medications = mutableListOf<Medication>()

        // Check if we have valid data
        if (response.drugGroup == null) {
            println("DEBUG: drugGroup is null")
            return emptyList()
        }

        if (response.drugGroup.conceptGroup.isNullOrEmpty()) {
            println("DEBUG: conceptGroup is null or empty")
            return emptyList()
        }

        response.drugGroup.conceptGroup.forEach { conceptGroup ->
            conceptGroup.conceptProperties?.forEach { concept ->
                val medicationName = concept.name ?: "Unknown Drug"
                println("DEBUG: Processing medication: $medicationName")

                val medication = Medication(
                    id = "rxnorm_${concept.rxcui ?: "unknown"}",
                    name = medicationName,
                    dosage = "",
                    strength = extractStrength(concept.name ?: ""),
                    type = concept.tty ?: "Unknown",
                    description = buildDescription(concept),
                    interactions = getInteractionsForDrug(medicationName),
                    source = "RxNorm API"
                )
                medications.add(medication)
            }
        }

        return medications
    }

    private fun extractStrength(drugName: String): String {
        // Extract strength from drug name if present (e.g., "Aspirin 81mg" -> "81mg")
        val strengthRegex = """\b(\d+\s*(mg|mcg|g|ml))\b""".toRegex(RegexOption.IGNORE_CASE)
        return strengthRegex.find(drugName)?.value ?: ""
    }

    private fun buildDescription(concept: ConceptProperty): String {
        val description = StringBuilder()

        description.append("RxNorm ID: ${concept.rxcui ?: "N/A"}")

        if (!concept.tty.isNullOrEmpty()) {
            description.append(" | Type: ${concept.tty}")
        }

        if (!concept.synonym.isNullOrEmpty()) {
            description.append(" | Also known as: ${concept.synonym}")
        }

        return description.toString()
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
                )
            )
            "ibuprofen" -> listOf(
                DrugInteraction(
                    medication1 = "Ibuprofen",
                    medication2 = "Aspirin",
                    severity = InteractionSeverity.MEDIUM,
                    description = "May reduce aspirin's cardioprotective effects",
                    recommendations = listOf(
                        "Take ibuprofen 2 hours after aspirin",
                        "Consider acetaminophen instead"
                    )
                )
            )
            else -> emptyList()
        }
    }
}
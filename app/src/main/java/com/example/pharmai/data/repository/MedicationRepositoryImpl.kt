package com.example.pharmai.data.repository

import com.example.pharmai.data.local.database.dao.MedicationDao
import com.example.pharmai.data.remote.api.MedicationApi
import com.example.pharmai.domain.model.Medication
import com.example.pharmai.domain.model.DrugInteraction
import com.example.pharmai.domain.model.InteractionSeverity
import com.example.pharmai.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(
    private val medicationDao: MedicationDao,
    private val medicationApi: MedicationApi
) : MedicationRepository {

    override suspend fun searchMedications(query: String): List<Medication> {
        // Simulate API/database search with delay
        kotlinx.coroutines.delay(500) // Simulate network delay

        return if (query.isBlank()) {
            emptyList()
        } else {
            mockMedications.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.type.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
    }

    override fun searchMedicationsFlow(query: String): Flow<List<Medication>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        // Simulate network call
        kotlinx.coroutines.delay(500)

        val results = mockMedications.filter {
            it.name.contains(query, ignoreCase = true)
        }
        emit(results)
    }

    override suspend fun getMedicationById(id: String): Medication? {
        return mockMedications.find { it.id == id }
    }

    override suspend fun checkInteractions(medication1: String, medication2: String): DrugInteraction? {
        // Simulate API call delay
        kotlinx.coroutines.delay(1000)

        return createMockInteraction(medication1, medication2)
    }

    override fun getUserMedications(): Flow<List<Medication>> {
        // For demo, return mock data as Flow
        return flow {
            emit(mockMedications.take(2)) // Return first 2 as user's medications
        }
    }

    override suspend fun addUserMedication(medication: Medication) {
        // In real app, save to database
        // For now, just print to log
        println("Added medication: ${medication.name}")
    }

    override suspend fun removeUserMedication(medicationId: String) {
        // In real app, remove from database
        // For now, just print to log
        println("Removed medication: $medicationId")
    }

    private fun createMockInteraction(med1: String, med2: String): DrugInteraction {
        val lowerMed1 = med1.lowercase()
        val lowerMed2 = med2.lowercase()

        return when {
            ("warfarin" in lowerMed1 && "aspirin" in lowerMed2) ||
                    ("aspirin" in lowerMed1 && "warfarin" in lowerMed2) -> {
                DrugInteraction(
                    medication1 = med1,
                    medication2 = med2,
                    severity = InteractionSeverity.HIGH,
                    description = "Increased risk of bleeding when warfarin is taken with aspirin.",
                    recommendations = listOf(
                        "Monitor for signs of bleeding",
                        "Consider alternative pain relief",
                        "Consult your doctor before combining"
                    ),
                    riskFactors = listOf(
                        "Increased bleeding risk",
                        "Reduced platelet function"
                    )
                )
            }
            ("lisinopril" in lowerMed1 && "ibuprofen" in lowerMed2) ||
                    ("ibuprofen" in lowerMed1 && "lisinopril" in lowerMed2) -> {
                DrugInteraction(
                    medication1 = med1,
                    medication2 = med2,
                    severity = InteractionSeverity.MEDIUM,
                    description = "NSAIDs may reduce the blood pressure lowering effects of ACE inhibitors.",
                    recommendations = listOf(
                        "Monitor blood pressure regularly",
                        "Use lowest effective NSAID dose",
                        "Consider acetaminophen as alternative"
                    ),
                    riskFactors = listOf(
                        "Reduced antihypertensive effect",
                        "Potential kidney function impact"
                    )
                )
            }
            ("simvastatin" in lowerMed1 && "grapefruit" in lowerMed2) ||
                    ("grapefruit" in lowerMed1 && "simvastatin" in lowerMed2) -> {
                DrugInteraction(
                    medication1 = med1,
                    medication2 = med2,
                    severity = InteractionSeverity.HIGH,
                    description = "Grapefruit can significantly increase simvastatin levels in blood.",
                    recommendations = listOf(
                        "Avoid grapefruit products",
                        "Consider alternative statin",
                        "Monitor for muscle pain or weakness"
                    ),
                    riskFactors = listOf(
                        "Increased risk of muscle damage",
                        "Potential liver toxicity"
                    )
                )
            }
            else -> {
                DrugInteraction(
                    medication1 = med1,
                    medication2 = med2,
                    severity = InteractionSeverity.LOW,
                    description = "No significant interactions found. Always consult your healthcare provider.",
                    recommendations = listOf(
                        "Continue monitoring as usual",
                        "Report any unusual symptoms"
                    ),
                    riskFactors = emptyList()
                )
            }
        }
    }

    companion object {
        private val mockMedications = listOf(
            Medication(
                id = "1",
                name = "Aspirin",
                dosage = "81mg",
                strength = "Low",
                type = "NSAID",
                description = "Pain reliever and anti-inflammatory medication"
            ),
            Medication(
                id = "2",
                name = "Lisinopril",
                dosage = "10mg",
                strength = "Medium",
                type = "Antihypertensive",
                description = "ACE inhibitor for high blood pressure"
            ),
            Medication(
                id = "3",
                name = "Metformin",
                dosage = "500mg",
                strength = "Medium",
                type = "Antidiabetic",
                description = "Oral diabetes medicine"
            ),
            Medication(
                id = "4",
                name = "Atorvastatin",
                dosage = "20mg",
                strength = "High",
                type = "Statin",
                description = "Lowers cholesterol and reduces risk of heart attack"
            ),
            Medication(
                id = "5",
                name = "Levothyroxine",
                dosage = "50mcg",
                strength = "Medium",
                type = "Thyroid Hormone",
                description = "Treats hypothyroidism (underactive thyroid)"
            ),
            Medication(
                id = "6",
                name = "Warfarin",
                dosage = "5mg",
                strength = "High",
                type = "Anticoagulant",
                description = "Blood thinner to prevent blood clots"
            ),
            Medication(
                id = "7",
                name = "Ibuprofen",
                dosage = "400mg",
                strength = "Medium",
                type = "NSAID",
                description = "Non-steroidal anti-inflammatory drug for pain and inflammation"
            ),
            Medication(
                id = "8",
                name = "Simvastatin",
                dosage = "40mg",
                strength = "High",
                type = "Statin",
                description = "Lowers cholesterol levels in the blood"
            )
        )
    }
}

// Extension function to convert entity to domain model (if needed later)
private fun com.example.pharmai.data.local.database.entity.MedicationEntity.toMedication(): Medication {
    return Medication(
        id = this.id,
        name = this.name,
        dosage = this.dosage,
        strength = this.strength,
        type = this.type,
        description = this.description,
        imageUrl = this.imageUrl
    )
}
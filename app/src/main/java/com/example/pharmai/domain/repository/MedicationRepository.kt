package com.example.pharmai.domain.repository

import com.example.pharmai.domain.model.Medication
import com.example.pharmai.domain.model.DrugInteraction
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    suspend fun searchMedications(query: String): List<Medication>

    // Alternative Flow-based approach
    fun searchMedicationsFlow(query: String): Flow<List<Medication>>

    suspend fun getMedicationById(id: String): Medication?
    suspend fun checkInteractions(medication1: String, medicationString: String): DrugInteraction?
    fun getUserMedications(): Flow<List<Medication>>
    suspend fun addUserMedication(medication: Medication)
    suspend fun removeUserMedication(medicationId: String)
}
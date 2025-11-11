package com.example.pharmai.domain.usecase

import com.example.pharmai.domain.model.Medication
import com.example.pharmai.domain.repository.MedicationRepository
import javax.inject.Inject

class SearchMedicationsUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(query: String): List<Medication> {
        return medicationRepository.searchMedications(query)
    }
}
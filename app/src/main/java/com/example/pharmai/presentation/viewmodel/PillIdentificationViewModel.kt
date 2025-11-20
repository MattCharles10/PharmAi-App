
package com.example.pharmai.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmai.domain.model.Pill
import com.example.pharmai.domain.model.PillIdentificationState
import com.example.pharmai.domain.model.PillSearchCriteria
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PillIdentificationViewModel : ViewModel() {

    private val _pillState = mutableStateOf(PillIdentificationState())
    val pillState: State<PillIdentificationState> = _pillState

    private val _searchCriteria = mutableStateOf(PillSearchCriteria())
    val searchCriteria: State<PillSearchCriteria> = _searchCriteria

    private val _showCamera = mutableStateOf(false)
    val showCamera: State<Boolean> = _showCamera

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Mock pill database
    private val mockPillDatabase = listOf(
        Pill(
            id = "1",
            name = "Aspirin",
            imprint = "BAYER",
            color = "White",
            shape = "Round",
            strength = "81 mg",
            manufacturer = "Bayer",
            description = "Pain reliever and anti-inflammatory medication",
            uses = "Used to treat pain, fever, and inflammation. Also used to prevent blood clots.",
            warnings = "May cause stomach bleeding, especially when taken with alcohol."
        ),
        Pill(
            id = "2",
            name = "Lisinopril",
            imprint = "L10",
            color = "White",
            shape = "Oval",
            strength = "10 mg",
            manufacturer = "Various Manufacturers",
            description = "ACE inhibitor for high blood pressure treatment",
            uses = "Treats high blood pressure and heart failure. Helps prevent kidney problems in diabetes.",
            warnings = "May cause persistent cough, dizziness, or kidney problems."
        ),
        Pill(
            id = "3",
            name = "Metformin",
            imprint = "M500",
            color = "White",
            shape = "Round",
            strength = "500 mg",
            manufacturer = "Various Manufacturers",
            description = "Oral diabetes medicine that helps control blood sugar levels",
            uses = "Manages type 2 diabetes. May be used for polycystic ovary syndrome.",
            warnings = "May cause stomach upset, diarrhea, or lactic acidosis in rare cases."
        ),
        Pill(
            id = "4",
            name = "Atorvastatin",
            imprint = "A20",
            color = "White",
            shape = "Oval",
            strength = "20 mg",
            manufacturer = "Pfizer",
            description = "Statin medication that lowers cholesterol levels",
            uses = "Lowers bad cholesterol and triglycerides. Raises good cholesterol.",
            warnings = "May cause muscle pain, liver problems, or increased blood sugar."
        ),
        Pill(
            id = "5",
            name = "Levothyroxine",
            imprint = "L50",
            color = "White",
            shape = "Round",
            strength = "50 mcg",
            manufacturer = "Various Manufacturers",
            description = "Thyroid hormone replacement therapy",
            uses = "Treats hypothyroidism (underactive thyroid). May prevent goiter.",
            warnings = "Take on empty stomach 30-60 minutes before food. Do not stop abruptly."
        ),
        Pill(
            id = "6",
            name = "Amoxicillin",
            imprint = "AMOX500",
            color = "Pink/White",
            shape = "Capsule",
            strength = "500 mg",
            manufacturer = "Various Manufacturers",
            description = "Penicillin antibiotic that fights bacterial infections",
            uses = "Treats various bacterial infections including ear, nose, throat, and urinary tract.",
            warnings = "Complete full course. May cause allergic reactions in penicillin-sensitive individuals."
        ),
        Pill(
            id = "7",
            name = "Ibuprofen",
            imprint = "IBU400",
            color = "White",
            shape = "Round",
            strength = "400 mg",
            manufacturer = "Various Manufacturers",
            description = "Nonsteroidal anti-inflammatory drug (NSAID)",
            uses = "Relieves pain, reduces inflammation, and lowers fever.",
            warnings = "May increase risk of heart attack, stroke, or stomach bleeding."
        )
    )

    fun searchPillsByCriteria(criteria: PillSearchCriteria) {
        viewModelScope.launch {
            _pillState.value = _pillState.value.copy(isLoading = true)
            _errorMessage.value = null

            try {
                delay(800) // Simulate API call

                val results = mockPillDatabase.filter { pill ->
                    (criteria.imprint.isBlank() || pill.imprint.contains(criteria.imprint, ignoreCase = true)) &&
                            (criteria.color.isBlank() || pill.color.equals(criteria.color, ignoreCase = true)) &&
                            (criteria.shape.isBlank() || pill.shape.equals(criteria.shape, ignoreCase = true))
                }

                _pillState.value = _pillState.value.copy(
                    identifiedPills = results,
                    isLoading = false
                )
            } catch (e: Exception) {
                _pillState.value = _pillState.value.copy(isLoading = false)
                _errorMessage.value = "Search failed: ${e.message}"
            }
        }
    }

    fun identifyPillFromImage(imageData: ByteArray) {
        viewModelScope.launch {
            _pillState.value = _pillState.value.copy(isLoading = true)
            _errorMessage.value = null

            try {
                delay(2000) // Simulate ML processing

                // Mock AI identification
                if (imageData.isEmpty()) {
                    throw IllegalArgumentException("Empty image data")
                }

                val randomPill = mockPillDatabase.random()
                val confidence = (70f + (Math.random() * 25)).toFloat()

                val identifiedPill = randomPill.copy(
                    confidence = confidence.coerceAtMost(1f)
                )

                _pillState.value = _pillState.value.copy(
                    identifiedPills = listOf(identifiedPill),
                    isLoading = false,
                    isCameraActive = false
                )
                _showCamera.value = false
            } catch (e: Exception) {
                _pillState.value = _pillState.value.copy(
                    isLoading = false,
                    isCameraActive = false
                )
                _showCamera.value = false
                _errorMessage.value = "Identification failed: ${e.message}"
            }
        }
    }

    fun updateSearchCriteria(criteria: PillSearchCriteria) {
        _searchCriteria.value = criteria
    }

    fun showCamera() {
        _showCamera.value = true
        _pillState.value = _pillState.value.copy(isCameraActive = true)
        _errorMessage.value = null
    }

    fun hideCamera() {
        _showCamera.value = false
        _pillState.value = _pillState.value.copy(isCameraActive = false)
    }

    fun clearResults() {
        _pillState.value = _pillState.value.copy(identifiedPills = emptyList())
        _searchCriteria.value = PillSearchCriteria()
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setCameraPermission(granted: Boolean) {
        _pillState.value = _pillState.value.copy(cameraPermissionGranted = granted)
    }
}
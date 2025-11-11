package com.example.pharmai.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FDAMedicationApi {
    @GET("drug/drugsfda.json")
    suspend fun searchMedications(
        @Query("search") query: String,
        @Query("limit") limit: Int = 20
    ): FDAResponse
}

data class FDAResponse(
    val results: List<FDAMedication>
)

data class FDAMedication(
    val product_ndc: String,
    val generic_name: String,
    val brand_name: String,
    val active_ingredients: List<ActiveIngredient>,
    val dosage_form: String,
    val route: String
)

data class ActiveIngredient(
    val name: String,
    val strength: String
)
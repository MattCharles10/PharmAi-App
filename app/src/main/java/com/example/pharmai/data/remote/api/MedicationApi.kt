package com.example.pharmai.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

interface MedicationApi {
    // We'll define these later when we have actual API endpoints
    // For now, this is a placeholder for future API integration
    @GET("medications/search")
    suspend fun searchMedications(@Query("query") query: String): List<Any> // Placeholder

    @GET("interactions/check")
    suspend fun checkInteractions(
        @Query("med1") medication1: String,
        @Query("med2") medication2: String
    ): Any // Placeholder
}
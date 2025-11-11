package com.example.pharmai.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.example.com/" // Replace with your actual API URL

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Create API service instances
    val medicationApi: MedicationApi by lazy {
        instance.create(MedicationApi::class.java)
    }

    val authApi: AuthApi by lazy {
        instance.create(AuthApi::class.java)
    }
}
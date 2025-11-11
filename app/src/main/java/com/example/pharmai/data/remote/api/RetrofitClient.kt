package com.example.pharmai.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://rxnav.nlm.nih.gov/REST/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Change to BODY for detailed logging
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val rxNormApi: RxNormApi = retrofit.create(RxNormApi::class.java)
}

interface RxNormApi {
    @GET("drugs.json")
    suspend fun searchDrugs(
        @Query("name") name: String
    ): RxNormDrugResponse
}

// Response Data Classes - CORRECTED FOR ACTUAL API RESPONSE
data class RxNormDrugResponse(
    val drugGroup: DrugGroup?
)

data class DrugGroup(
    val name: String?,
    val conceptGroup: List<ConceptGroup>?
)

data class ConceptGroup(
    val tty: String?,
    val conceptProperties: List<ConceptProperty>?
)

data class ConceptProperty(
    val rxcui: String?,
    val name: String?,
    val synonym: String?,
    val tty: String?,
    val language: String?,
    val suppress: String?,
    val umlscui: String?
)
package com.example.pharmai.data.local.database.dao

import androidx.room.*
import com.example.pharmai.data.local.database.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE userId = :userId")
    fun getUserMedications(userId: String): Flow<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Query("DELETE FROM medications WHERE id = :medicationId")
    suspend fun deleteMedication(medicationId: String)

    @Query("SELECT * FROM medications WHERE name LIKE '%' || :query || '%'")
    suspend fun searchMedications(query: String): List<MedicationEntity>
}

package com.example.pharmai.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val dosage: String,
    val strength: String,
    val type: String,
    val description: String,
    val imageUrl: String? = null,
    val userId: String? = null
)
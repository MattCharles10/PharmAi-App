package com.example.pharmai.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.pharmai.data.local.database.dao.MedicationDao
import com.example.pharmai.data.local.database.dao.UserDao
import com.example.pharmai.data.local.database.entity.MedicationEntity
import com.example.pharmai.data.local.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, MedicationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pharmai_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
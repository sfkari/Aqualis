package com.cmc.aqualis.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entité User pour Room
@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val password: String,
    val lastName: String,
    val firstName: String,
    val birthDate: String,
    val gender: String,
    val weight: Double,
    val height: Double
)
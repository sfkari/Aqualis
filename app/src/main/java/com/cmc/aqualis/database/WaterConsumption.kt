package com.cmc.aqualis.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "water_consumption")
data class WaterConsumption(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val amount: Double,
    val date: Date
)

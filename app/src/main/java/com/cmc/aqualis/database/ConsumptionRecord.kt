package com.cmc.aqualis.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consumption_records")
data class ConsumptionRecord(
    @PrimaryKey(autoGenerate = true) 
    @ColumnInfo(name = "id")
    val id: Int = 0,  // Clé primaire auto-générée
    
    @ColumnInfo(name = "amount")
    val amount: Int,  // Quantité d'eau en ml
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long  // Date et heure de consommation
)

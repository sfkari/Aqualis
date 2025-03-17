package com.cmc.aqualis.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.Date

@Dao
interface WaterConsumptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waterConsumption: WaterConsumption)

    @Query("SELECT * FROM water_consumption WHERE userId = :userId AND date = :date")
    suspend fun getConsumptionByUserAndDate(userId: String, date: Long): List<WaterConsumption>

    @Query("SELECT SUM(amount) FROM water_consumption WHERE userId = :userId AND date = :date")
    suspend fun getTotalConsumptionByUserAndDate(userId: String, date: Long): Int?

    @Query("SELECT * FROM water_consumption WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllConsumptionsByUser(userId: String): List<WaterConsumption>
    
    // Nouvelles méthodes pour le tableau de bord
    
    @Query("SELECT SUM(amount) FROM water_consumption WHERE userId = :userId")
    suspend fun getTotalConsumption(userId: String): Double
    
    @Query("SELECT SUM(amount) FROM water_consumption WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalConsumptionBetweenDates(userId: String, startDate: Date, endDate: Date): Double
    
    @Query("SELECT SUM(amount) FROM water_consumption WHERE userId = :userId AND date = :date")
    suspend fun getTotalConsumptionForDay(userId: String, date: Date): Double
    
    @Query("SELECT * FROM water_consumption WHERE userId = :userId AND date >= :startDate ORDER BY date ASC")
    suspend fun getConsumptionSinceDate(userId: String, startDate: Date): List<WaterConsumption>
    
    @Query("SELECT date, SUM(amount) as total FROM water_consumption WHERE userId = :userId AND date >= :startDate AND date <= :endDate GROUP BY strftime('%Y-%m-%d', date/1000, 'unixepoch') ORDER BY date ASC")
    suspend fun getDailyConsumptionBetweenDates(userId: String, startDate: Date, endDate: Date): List<DailyConsumption>
    
    @Query("SELECT COUNT(DISTINCT strftime('%Y-%m-%d', date/1000, 'unixepoch')) FROM water_consumption WHERE userId = :userId")
    suspend fun getNumberOfDaysWithConsumption(userId: String): Int
}

// Classe pour représenter la consommation quotidienne
data class DailyConsumption(
    val date: Date,
    val total: Double
)

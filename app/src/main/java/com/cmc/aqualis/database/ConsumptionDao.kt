package com.cmc.aqualis.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConsumptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record: ConsumptionRecord): Long

    @Query("SELECT * FROM consumption_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<ConsumptionRecord>>

    @Query("SELECT * FROM consumption_records ORDER BY timestamp DESC")
    fun getAllRecordsDirect(): List<ConsumptionRecord>
}

package com.cmc.aqualis.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, WaterConsumption::class], version = 1)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun waterConsumptionDao(): WaterConsumptionDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.cmc.aqualis.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?


    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun isEmailExists(email: String): Boolean

}

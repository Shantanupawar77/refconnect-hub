package com.example.refconnect.data.local.dao

import androidx.room.*
import com.example.refconnect.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("SELECT * FROM profiles WHERE userId = :userId")
    suspend fun getProfileByUserId(userId: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE userId = :userId")
    fun getProfileByUserIdFlow(userId: String): Flow<ProfileEntity?>

    @Query("DELETE FROM profiles WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}

package com.example.refconnect.data.local.dao

import androidx.room.*
import com.example.refconnect.data.local.entity.ReferralEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReferralDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(referral: ReferralEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(referrals: List<ReferralEntity>)

    @Update
    suspend fun update(referral: ReferralEntity)

    @Delete
    suspend fun delete(referral: ReferralEntity)

    @Query("SELECT * FROM referrals WHERE id = :referralId")
    suspend fun getReferralById(referralId: String): ReferralEntity?

    @Query("SELECT * FROM referrals ORDER BY createdAt DESC")
    fun getAllReferrals(): Flow<List<ReferralEntity>>

    @Query("SELECT * FROM referrals WHERE postedByUserId = :userId ORDER BY createdAt DESC")
    fun getReferralsByUser(userId: String): Flow<List<ReferralEntity>>

    @Query("SELECT * FROM referrals WHERE difficulty = :difficulty ORDER BY createdAt DESC")
    fun getReferralsByDifficulty(difficulty: String): Flow<List<ReferralEntity>>

    @Query("SELECT * FROM referrals WHERE techTag = :techTag ORDER BY createdAt DESC")
    fun getReferralsByTechTag(techTag: String): Flow<List<ReferralEntity>>

    @Query("DELETE FROM referrals WHERE id = :referralId")
    suspend fun deleteById(referralId: String)

    @Query("DELETE FROM referrals")
    suspend fun deleteAll()
}

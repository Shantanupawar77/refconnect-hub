package com.example.refconnect.data.local.dao

import androidx.room.*
import com.example.refconnect.data.local.entity.ScreeningTestEntity

@Dao
interface ScreeningTestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(test: ScreeningTestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tests: List<ScreeningTestEntity>)

    @Query("SELECT * FROM screening_tests WHERE id = :testId")
    suspend fun getTestById(testId: String): ScreeningTestEntity?

    @Query("SELECT * FROM screening_tests WHERE referralId = :referralId")
    suspend fun getTestByReferralId(referralId: String): ScreeningTestEntity?

    @Query("DELETE FROM screening_tests WHERE referralId = :referralId")
    suspend fun deleteByReferralId(referralId: String)
}

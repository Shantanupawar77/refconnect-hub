package com.example.refconnect.data.local.dao

import androidx.room.*
import com.example.refconnect.data.local.entity.TestAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: TestAttemptEntity)

    @Query("SELECT * FROM test_attempts WHERE userId = :userId AND testId = :testId ORDER BY attemptedAt DESC")
    suspend fun getAttemptsByUserAndTest(userId: String, testId: String): List<TestAttemptEntity>

    @Query("SELECT * FROM test_attempts WHERE userId = :userId AND testId = :testId AND passed = 1 ORDER BY attemptedAt DESC LIMIT 1")
    suspend fun getPassedAttempt(userId: String, testId: String): TestAttemptEntity?

    @Query("SELECT * FROM test_attempts WHERE userId = :userId AND testId = :testId ORDER BY attemptedAt DESC LIMIT 1")
    suspend fun getLatestAttempt(userId: String, testId: String): TestAttemptEntity?

    @Query("SELECT * FROM test_attempts WHERE userId = :userId ORDER BY attemptedAt DESC")
    fun getAttemptsByUser(userId: String): Flow<List<TestAttemptEntity>>

    @Query("DELETE FROM test_attempts WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}
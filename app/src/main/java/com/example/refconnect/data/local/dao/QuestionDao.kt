package com.example.refconnect.data.local.dao

import androidx.room.*
import com.example.refconnect.data.local.entity.QuestionEntity

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY orderIndex ASC")
    suspend fun getQuestionsByTestId(testId: String): List<QuestionEntity>

    @Query("DELETE FROM questions WHERE testId = :testId")
    suspend fun deleteByTestId(testId: String)
}
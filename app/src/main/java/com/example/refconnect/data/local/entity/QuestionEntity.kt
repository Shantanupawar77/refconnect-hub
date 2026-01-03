package com.example.refconnect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = ScreeningTestEntity::class,
            parentColumns = ["id"],
            childColumns = ["testId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("testId")]
)
data class QuestionEntity(
    @PrimaryKey
    val id: String,
    val testId: String,
    val questionText: String,
    val options: String, // JSON array of options
    val correctAnswerIndex: Int,
    val orderIndex: Int = 0 // To maintain question order
)

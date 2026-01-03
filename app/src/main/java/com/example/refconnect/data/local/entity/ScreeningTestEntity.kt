package com.example.refconnect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "screening_tests",
    foreignKeys = [
        ForeignKey(
            entity = ReferralEntity::class,
            parentColumns = ["id"],
            childColumns = ["referralId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("referralId")]
)
data class ScreeningTestEntity(
    @PrimaryKey
    val id: String,
    val referralId: String,
    val passThreshold: Int = 60, // Percentage needed to pass
    val timeLimit: Int? = null // Optional time limit in minutes
)

package com.example.refconnect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "referrals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["postedByUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("postedByUserId")]
)
data class ReferralEntity(
    @PrimaryKey
    val id: String,
    val postedByUserId: String,
    val postedByUserName: String,
    val company: String,
    val role: String,
    val description: String,
    val techStack: String, // Comma-separated
    val experienceRequired: Int,
    val location: String = "Remote",
    val difficulty: String = "MEDIUM", // EASY, MEDIUM, HARD
    val techTag: String = "GENERAL", // ANDROID, BACKEND, FRONTEND, DATA_SCIENCE, etc.
    val companyLogoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

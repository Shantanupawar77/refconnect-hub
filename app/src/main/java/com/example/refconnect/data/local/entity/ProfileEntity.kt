package com.example.refconnect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ProfileEntity(
    @PrimaryKey
    val userId: String,
    val company: String,
    val experience: Int,
    val techStack: String, // Stored as comma-separated string
    val leetcodeUrl: String = "",
    val codeforcesUrl: String = "",
    val codechefUrl: String = "",
    val githubUrl: String = "",
    val hackathonExperience: String = "",
    val achievements: String = "",
    val profileSetupComplete: Boolean = false
)

package com.example.refconnect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "connections",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["requesterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["referralGiverId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ReferralEntity::class,
            parentColumns = ["id"],
            childColumns = ["referralId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("requesterId"),
        Index("referralGiverId"),
        Index("referralId"),
        Index(value = ["requesterId", "referralId"], unique = true)
    ]
)
data class ConnectionEntity(
    @PrimaryKey
    val id: String,
    val requesterId: String,
    val requesterName: String,
    val referralGiverId: String,
    val referralGiverName: String,
    val referralId: String,
    val referralRole: String,
    val status: String,
    val isUnreadByGiver: Boolean = true,
    val isUnreadBySeeker: Boolean = false,
    val requestedAt: Long = System.currentTimeMillis(),
    val respondedAt: Long? = null
)

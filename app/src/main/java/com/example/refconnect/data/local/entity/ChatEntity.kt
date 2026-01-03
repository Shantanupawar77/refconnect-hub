package com.example.refconnect.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chats",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user1Id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user2Id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user1Id"), Index("user2Id")]
)
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val user1Id: String,
    val user1Name: String,
    val user2Id: String,
    val user2Name: String,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCountUser1: Int = 0, // Unread count for user1
    val unreadCountUser2: Int = 0  // Unread count for user2
)
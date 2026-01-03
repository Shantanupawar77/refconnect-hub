package com.example.refconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

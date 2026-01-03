package com.example.refconnect.model

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Chat(
    val id: String,
    val user1Id: String,
    val user1Name: String,
    val user2Id: String,
    val user2Name: String,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)

package com.example.refconnect.data.local.dao

import androidx.room.*
import com.example.refconnect.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatEntity)

    @Update
    suspend fun update(chat: ChatEntity)

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): ChatEntity?

    @Query("SELECT * FROM chats WHERE user1Id = :userId OR user2Id = :userId ORDER BY lastMessageTime DESC")
    fun getChatsByUser(userId: String): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE (user1Id = :userId1 AND user2Id = :userId2) OR (user1Id = :userId2 AND user2Id = :userId1) LIMIT 1")
    suspend fun getChatByUsers(userId1: String, userId2: String): ChatEntity?

    @Query("SELECT SUM(CASE WHEN user1Id = :userId THEN unreadCountUser1 ELSE unreadCountUser2 END) FROM chats WHERE user1Id = :userId OR user2Id = :userId")
    fun getTotalUnreadCount(userId: String): Flow<Int>

    @Query("UPDATE chats SET unreadCountUser1 = 0 WHERE id = :chatId")
    suspend fun clearUnreadForUser1(chatId: String)

    @Query("UPDATE chats SET unreadCountUser2 = 0 WHERE id = :chatId")
    suspend fun clearUnreadForUser2(chatId: String)

    @Query("DELETE FROM chats")
    suspend fun deleteAll()
}
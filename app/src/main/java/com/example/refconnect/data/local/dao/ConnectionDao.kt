package com.example.refconnect.data.local.dao

import androidx.room.*
import com.example.refconnect.data.local.entity.ConnectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(connection: ConnectionEntity)

    @Update
    suspend fun update(connection: ConnectionEntity)

    @Query("SELECT * FROM connections WHERE id = :connectionId")
    suspend fun getConnectionById(connectionId: String): ConnectionEntity?

    @Query("SELECT * FROM connections WHERE requesterId = :userId OR referralGiverId = :userId ORDER BY requestedAt DESC")
    fun getConnectionsByUser(userId: String): Flow<List<ConnectionEntity>>

    @Query("SELECT * FROM connections WHERE referralGiverId = :userId AND status = 'PENDING' AND isUnreadByGiver = 1 ORDER BY requestedAt DESC")
    fun getPendingUnreadRequestsForGiver(userId: String): Flow<List<ConnectionEntity>>

    @Query("SELECT * FROM connections WHERE referralGiverId = :userId AND status = 'PENDING' ORDER BY requestedAt DESC")
    fun getPendingRequestsForGiver(userId: String): Flow<List<ConnectionEntity>>

    @Query("SELECT * FROM connections WHERE referralId = :referralId ORDER BY requestedAt DESC")
    suspend fun getConnectionsByReferral(referralId: String): List<ConnectionEntity>

    @Query("SELECT COUNT(*) FROM connections WHERE referralGiverId = :userId AND isUnreadByGiver = 1 AND status = 'PENDING'")
    fun getUnreadCountForGiver(userId: String): Flow<Int>

    @Query("UPDATE connections SET isUnreadByGiver = 0 WHERE referralGiverId = :userId")
    suspend fun markAllAsReadForGiver(userId: String)

    @Query("UPDATE connections SET isUnreadBySeeker = 0 WHERE requesterId = :userId")
    suspend fun markAllAsReadForSeeker(userId: String)

    @Query("DELETE FROM connections")
    suspend fun deleteAll()
}
package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.ConsumerMilletRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumerMilletRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(request: ConsumerMilletRequestEntity): Long

    @Query("SELECT * FROM consumer_millet_requests WHERE consumerUserId = :userId AND status = 'ACTIVE' ORDER BY createdAt DESC LIMIT 1")
    fun observeActiveByConsumer(userId: String): Flow<ConsumerMilletRequestEntity?>

    @Query("SELECT * FROM consumer_millet_requests WHERE consumerUserId = :userId AND status = 'ACTIVE' ORDER BY createdAt DESC LIMIT 1")
    suspend fun getActiveByConsumer(userId: String): ConsumerMilletRequestEntity?

    @Query("UPDATE consumer_millet_requests SET status = :status WHERE id = :requestId")
    suspend fun updateStatus(requestId: Int, status: String)

    @Query("SELECT * FROM consumer_millet_requests WHERE id = :requestId LIMIT 1")
    suspend fun getById(requestId: Int): ConsumerMilletRequestEntity?
}

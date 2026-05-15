package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feedback: FeedbackEntity)

    @Query("SELECT * FROM feedback_records WHERE farmerUserId = :farmerUserId ORDER BY createdAt DESC")
    fun observeByFarmer(farmerUserId: String): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedback_records WHERE consumerUserId = :consumerUserId ORDER BY createdAt DESC")
    fun observeByConsumer(consumerUserId: String): Flow<List<FeedbackEntity>>

    @Query("SELECT AVG(rating) FROM feedback_records WHERE farmerUserId = :farmerUserId")
    suspend fun averageRatingForFarmer(farmerUserId: String): Double?

    @Query("SELECT COUNT(*) FROM feedback_records WHERE farmerUserId = :farmerUserId")
    suspend fun countForFarmer(farmerUserId: String): Int
}
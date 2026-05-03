package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: TransactionRecordEntity)

    @Query("SELECT * FROM transaction_records WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeByUserId(userId: String): Flow<List<TransactionRecordEntity>>

    @Query("SELECT COUNT(*) FROM transaction_records WHERE userId = :userId")
    suspend fun countByUserId(userId: String): Int
}

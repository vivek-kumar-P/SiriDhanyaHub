package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.FpoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FpoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FpoEntity>)

    @Query("SELECT COUNT(*) FROM fpos")
    suspend fun count(): Int

    @Query("SELECT * FROM fpos ORDER BY district, fpoName")
    fun observeAll(): Flow<List<FpoEntity>>

    @Query("SELECT * FROM fpos WHERE id = :fpoId LIMIT 1")
    fun observeById(fpoId: Int): Flow<FpoEntity?>
}


package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.HealthBenefitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthBenefitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<HealthBenefitEntity>)

    @Query("SELECT COUNT(*) FROM health_benefits")
    suspend fun count(): Int

    @Query("SELECT * FROM health_benefits ORDER BY milletType")
    fun observeAll(): Flow<List<HealthBenefitEntity>>

    @Query("SELECT * FROM health_benefits WHERE milletType = :milletType")
    fun observeByMilletType(milletType: String): Flow<List<HealthBenefitEntity>>
}


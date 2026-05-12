package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): UserProfileEntity?

    @Query("SELECT COUNT(*) FROM user_profiles WHERE contactNumber = :contactNumber AND userId != :userId")
    suspend fun countByContactNumber(contactNumber: String, userId: String): Int

    @Query("SELECT COUNT(*) FROM user_profiles WHERE aadhaarLast4 = :aadhaarLast4 AND userId != :userId")
    suspend fun countByAadhaarLast4(aadhaarLast4: String, userId: String): Int

    @Query("SELECT COUNT(*) FROM user_profiles WHERE pmKisanOrFarmerId = :farmerId AND userId != :userId")
    suspend fun countByFarmerId(farmerId: String, userId: String): Int
}

package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.FarmerStockListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerStockListingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(listing: FarmerStockListingEntity)

    @Query("SELECT * FROM farmer_stock_listings WHERE farmerUserId = :userId LIMIT 1")
    fun observeByFarmer(userId: String): Flow<FarmerStockListingEntity?>

    @Query("SELECT * FROM farmer_stock_listings WHERE farmerUserId = :userId LIMIT 1")
    suspend fun getByFarmer(userId: String): FarmerStockListingEntity?

    @Query("SELECT * FROM farmer_stock_listings WHERE isActive = 1")
    suspend fun getAllActive(): List<FarmerStockListingEntity>
}

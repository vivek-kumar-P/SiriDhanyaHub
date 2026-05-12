package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.FarmerRequestMatchEntity
import com.mindmatrix.siridhanyahub.data.local.model.FarmerInboxItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerRequestMatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FarmerRequestMatchEntity>)

    @Query("DELETE FROM farmer_request_matches WHERE requestId = :requestId")
    suspend fun deleteByRequestId(requestId: Int)

    @Query(
        """
        SELECT 
            m.id AS matchId,
            r.id AS requestId,
            m.farmerUserId AS farmerUserId,
            m.listingId AS listingId,
            m.matchLocationContext AS matchLocationContext,
            r.consumerUserId AS consumerUserId,
            p.fullName AS consumerName,
            p.contactNumber AS consumerMobile,
            p.address AS consumerAddress,
            p.district AS consumerDistrict,
            r.milletType AS milletType,
            r.quantityKg AS quantityKg,
            r.neededAtMillis AS neededAtMillis,
            r.preferredSourceLocation AS preferredSourceLocation,
            r.purpose AS purpose,
            r.status AS status
        FROM farmer_request_matches m
        INNER JOIN consumer_millet_requests r ON r.id = m.requestId
        INNER JOIN user_profiles p ON p.userId = r.consumerUserId
        WHERE m.farmerUserId = :farmerUserId AND r.status = 'ACTIVE'
        ORDER BY r.createdAt DESC
        """
    )
    fun observeInboxForFarmer(farmerUserId: String): Flow<List<FarmerInboxItem>>
}

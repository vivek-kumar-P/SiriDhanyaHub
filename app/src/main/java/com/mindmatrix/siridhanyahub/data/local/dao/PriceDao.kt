package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PriceEntity>)

    @Query("SELECT COUNT(*) FROM prices")
    suspend fun count(): Int

    @Query(
        """
        SELECT * FROM prices
        WHERE city = :city
        AND dateRecorded = (
            SELECT MAX(dateRecorded) FROM prices p2
            WHERE p2.milletType = prices.milletType AND p2.city = prices.city
        )
        ORDER BY milletType
        """
    )
    fun observeLatestPricesByCity(city: String): Flow<List<PriceEntity>>

    @Query(
        """
        SELECT * FROM prices
        WHERE milletType = :milletType AND city = :city
        ORDER BY dateRecorded DESC
        LIMIT 7
        """
    )
    fun observeSevenDayTrend(milletType: String, city: String): Flow<List<PriceEntity>>
}


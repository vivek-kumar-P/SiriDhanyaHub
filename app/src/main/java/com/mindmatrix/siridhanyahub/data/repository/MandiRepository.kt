package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.PriceDao
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MandiRepository @Inject constructor(
    private val priceDao: PriceDao
) {
    fun observeLatestPricesByCity(city: String): Flow<List<PriceEntity>> {
        return priceDao.observeLatestPricesByCity(city)
    }

    fun observeSevenDayTrend(milletType: String, city: String): Flow<List<PriceEntity>> {
        return priceDao.observeSevenDayTrend(milletType, city)
    }
}

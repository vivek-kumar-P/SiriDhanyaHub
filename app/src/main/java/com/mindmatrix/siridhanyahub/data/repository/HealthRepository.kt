package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.HealthBenefitDao
import com.mindmatrix.siridhanyahub.data.local.entity.HealthBenefitEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor(
    private val healthBenefitDao: HealthBenefitDao
) {
    fun observeAll(): Flow<List<HealthBenefitEntity>> = healthBenefitDao.observeAll()

    fun observeByMilletType(milletType: String): Flow<List<HealthBenefitEntity>> {
        return healthBenefitDao.observeByMilletType(milletType)
    }
}

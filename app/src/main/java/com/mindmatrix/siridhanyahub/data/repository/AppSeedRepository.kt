package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.FpoDao
import com.mindmatrix.siridhanyahub.data.local.dao.HealthBenefitDao
import com.mindmatrix.siridhanyahub.data.local.dao.PriceDao
import com.mindmatrix.siridhanyahub.data.local.dao.RecipeDao
import com.mindmatrix.siridhanyahub.data.seed.SeedBundle
import com.mindmatrix.siridhanyahub.data.seed.SeedDataProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSeedRepository @Inject constructor(
    private val priceDao: PriceDao,
    private val recipeDao: RecipeDao,
    private val healthBenefitDao: HealthBenefitDao,
    private val fpoDao: FpoDao
) {
    suspend fun seedIfNeeded(): SeedBundle {
        if (priceDao.count() == 0) {
            priceDao.insertAll(SeedDataProvider.priceSeeds())
        }

        if (recipeDao.count() == 0) {
            recipeDao.insertAll(SeedDataProvider.recipeSeeds())
        }

        if (healthBenefitDao.count() == 0) {
            healthBenefitDao.insertAll(SeedDataProvider.healthBenefitSeeds())
        }

        if (fpoDao.count() == 0) {
            fpoDao.insertAll(SeedDataProvider.fpoSeeds())
        }

        return SeedDataProvider.summary()
    }
}

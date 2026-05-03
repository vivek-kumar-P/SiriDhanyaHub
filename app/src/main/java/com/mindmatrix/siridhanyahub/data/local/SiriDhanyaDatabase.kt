package com.mindmatrix.siridhanyahub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mindmatrix.siridhanyahub.data.local.dao.FavouriteDao
import com.mindmatrix.siridhanyahub.data.local.dao.FpoDao
import com.mindmatrix.siridhanyahub.data.local.dao.HealthBenefitDao
import com.mindmatrix.siridhanyahub.data.local.dao.PriceDao
import com.mindmatrix.siridhanyahub.data.local.dao.RecipeDao
import com.mindmatrix.siridhanyahub.data.local.dao.TransactionRecordDao
import com.mindmatrix.siridhanyahub.data.local.dao.UserProfileDao
import com.mindmatrix.siridhanyahub.data.local.entity.FavouriteEntity
import com.mindmatrix.siridhanyahub.data.local.entity.FpoEntity
import com.mindmatrix.siridhanyahub.data.local.entity.HealthBenefitEntity
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import com.mindmatrix.siridhanyahub.data.local.entity.UserProfileEntity

@Database(
    entities = [
        PriceEntity::class,
        RecipeEntity::class,
        FavouriteEntity::class,
        HealthBenefitEntity::class,
        FpoEntity::class,
        UserProfileEntity::class,
        TransactionRecordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SiriDhanyaDatabase : RoomDatabase() {
    abstract fun priceDao(): PriceDao
    abstract fun recipeDao(): RecipeDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun healthBenefitDao(): HealthBenefitDao
    abstract fun fpoDao(): FpoDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun transactionRecordDao(): TransactionRecordDao

    companion object {
        const val DATABASE_NAME = "siri_dhanya_hub.db"
    }
}

package com.mindmatrix.siridhanyahub.di

import android.content.Context
import androidx.room.Room
import com.mindmatrix.siridhanyahub.data.local.SiriDhanyaDatabase
import com.mindmatrix.siridhanyahub.data.local.dao.FavouriteDao
import com.mindmatrix.siridhanyahub.data.local.dao.FpoDao
import com.mindmatrix.siridhanyahub.data.local.dao.HealthBenefitDao
import com.mindmatrix.siridhanyahub.data.local.dao.PriceDao
import com.mindmatrix.siridhanyahub.data.local.dao.RecipeDao
import com.mindmatrix.siridhanyahub.data.local.dao.TransactionRecordDao
import com.mindmatrix.siridhanyahub.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SiriDhanyaDatabase {
        return Room.databaseBuilder(
            context,
            SiriDhanyaDatabase::class.java,
            SiriDhanyaDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePriceDao(database: SiriDhanyaDatabase): PriceDao = database.priceDao()

    @Provides
    fun provideRecipeDao(database: SiriDhanyaDatabase): RecipeDao = database.recipeDao()

    @Provides
    fun provideFavouriteDao(database: SiriDhanyaDatabase): FavouriteDao = database.favouriteDao()

    @Provides
    fun provideHealthBenefitDao(database: SiriDhanyaDatabase): HealthBenefitDao =
        database.healthBenefitDao()

    @Provides
    fun provideFpoDao(database: SiriDhanyaDatabase): FpoDao = database.fpoDao()

    @Provides
    fun provideUserProfileDao(database: SiriDhanyaDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun provideTransactionRecordDao(database: SiriDhanyaDatabase): TransactionRecordDao =
        database.transactionRecordDao()
}

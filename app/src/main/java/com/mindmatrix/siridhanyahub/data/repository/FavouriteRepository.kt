package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.FavouriteDao
import com.mindmatrix.siridhanyahub.data.local.entity.FavouriteEntity
import com.mindmatrix.siridhanyahub.data.local.model.FavouriteRecipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class FavouriteRepository @Inject constructor(
    private val favouriteDao: FavouriteDao,
    private val authRepository: AuthRepository
) {
    fun observeFavouriteRecipes(): Flow<List<FavouriteRecipe>> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(emptyList()) else favouriteDao.observeFavouriteRecipes(session.uid)
        }

    fun observeIsFavourite(recipeId: Int): Flow<Boolean> = authRepository.currentUser
        .flatMapLatest { session ->
            if (session == null) flowOf(false) else favouriteDao.observeIsFavourite(session.uid, recipeId)
        }

    suspend fun save(recipeId: Int) {
        val session = authRepository.currentSessionSnapshot() ?: return
        favouriteDao.insert(
            FavouriteEntity(
                userId = session.uid,
                recipeId = recipeId,
                savedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun remove(recipeId: Int) {
        val session = authRepository.currentSessionSnapshot() ?: return
        favouriteDao.deleteByRecipeId(session.uid, recipeId)
    }
}

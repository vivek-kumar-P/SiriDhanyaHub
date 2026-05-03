package com.mindmatrix.siridhanyahub.data.repository

import com.mindmatrix.siridhanyahub.data.local.dao.RecipeDao
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    fun observeAll(): Flow<List<RecipeEntity>> = recipeDao.observeAll()

    fun search(query: String): Flow<List<RecipeEntity>> {
        return if (query.isBlank()) recipeDao.observeAll() else recipeDao.search(query)
    }

    fun observeById(recipeId: Int): Flow<RecipeEntity?> = recipeDao.observeById(recipeId)
}

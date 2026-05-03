package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mindmatrix.siridhanyahub.data.local.entity.FavouriteEntity
import com.mindmatrix.siridhanyahub.data.local.model.FavouriteRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun deleteByRecipeId(userId: String, recipeId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE userId = :userId AND recipeId = :recipeId)")
    fun observeIsFavourite(userId: String, recipeId: Int): Flow<Boolean>

    @Transaction
    @Query("SELECT * FROM favourites WHERE userId = :userId ORDER BY savedAt DESC")
    fun observeFavouriteRecipes(userId: String): Flow<List<FavouriteRecipe>>
}

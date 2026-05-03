package com.mindmatrix.siridhanyahub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RecipeEntity>)

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun count(): Int

    @Query("SELECT * FROM recipes ORDER BY milletType, nameEnglish")
    fun observeAll(): Flow<List<RecipeEntity>>

    @Query(
        """
        SELECT * FROM recipes
        WHERE milletType LIKE '%' || :query || '%'
        OR nameEnglish LIKE '%' || :query || '%'
        OR nameKannada LIKE '%' || :query || '%'
        ORDER BY nameEnglish
        """
    )
    fun search(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId LIMIT 1")
    fun observeById(recipeId: Int): Flow<RecipeEntity?>
}


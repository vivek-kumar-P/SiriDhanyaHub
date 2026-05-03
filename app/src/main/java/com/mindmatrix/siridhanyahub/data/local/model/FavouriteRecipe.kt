package com.mindmatrix.siridhanyahub.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.mindmatrix.siridhanyahub.data.local.entity.FavouriteEntity
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity

data class FavouriteRecipe(
    @Embedded val favourite: FavouriteEntity,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: RecipeEntity
)


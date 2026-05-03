package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourites",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "recipeId"], unique = true),
        Index(value = ["recipeId"])
    ]
)
data class FavouriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val recipeId: Int,
    val savedAt: Long
)

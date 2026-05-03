package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipes",
    indices = [Index(value = ["milletType"])]
)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameKannada: String,
    val nameEnglish: String,
    val milletType: String,
    val ingredientsKannada: String,
    val stepsKannada: String,
    val prepTimeMinutes: Int,
    val servesCount: Int,
    val imageUrl: String? = null
)


package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fpos")
data class FpoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fpoName: String,
    val district: String,
    val address: String,
    val phone: String,
    val availableMillets: String,
    val availableQuantityKg: Int
)


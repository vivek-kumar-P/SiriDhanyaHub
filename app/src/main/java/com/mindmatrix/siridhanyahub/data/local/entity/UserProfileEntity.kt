package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val phone: String,
    val district: String,
    val role: String,
    val primaryMillet: String,
    val updatedAt: Long = System.currentTimeMillis()
)

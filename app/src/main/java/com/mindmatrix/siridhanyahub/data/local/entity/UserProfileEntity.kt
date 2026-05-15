package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val role: String,
    val roleLocked: Boolean = false,
    val fullName: String,
    val address: String,
    val contactNumber: String,
    val aadhaarLast4: String,
    val pmKisanOrFarmerId: String? = null,
    val district: String,
    val taluk: String,
    val village: String,
    val primaryMilletCategory: String? = null,
    val landSizeAcres: Double? = null,
    val preferredPurchaseLocation: String? = null,
    val profileCompleted: Boolean = false,
    val stockStatus: String = "NO_STOCK",
    val updatedAt: Long = System.currentTimeMillis()
)

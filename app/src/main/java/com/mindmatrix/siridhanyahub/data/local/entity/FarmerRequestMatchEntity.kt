package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "farmer_request_matches",
    indices = [Index("requestId"), Index("farmerUserId"), Index("listingId")]
)
data class FarmerRequestMatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requestId: Int,
    val farmerUserId: String,
    val listingId: Int,
    val matchLocationContext: String,
    val createdAt: Long = System.currentTimeMillis()
)

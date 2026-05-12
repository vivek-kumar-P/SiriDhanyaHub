package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "farmer_stock_listings",
    indices = [Index(value = ["farmerUserId"], unique = true), Index("marketCity"), Index("milletType")]
)
data class FarmerStockListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val farmerUserId: String,
    val marketCity: String,
    val marketPlace: String,
    val selectedMarketRate: Double,
    val milletType: String,
    val quantityAvailableKg: Int,
    val growthDurationDays: Int,
    val grownArea: String,
    val stockNote: String,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

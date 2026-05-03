package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prices",
    indices = [
        Index(value = ["milletType", "city", "dateRecorded"], unique = true),
        Index(value = ["city"])
    ]
)
data class PriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val milletType: String,
    val city: String,
    val pricePerQuintal: Double,
    val dateRecorded: Long,
    val trendDirection: String,
    val lastUpdated: Long
)


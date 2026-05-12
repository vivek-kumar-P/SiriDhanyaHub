package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "consumer_millet_requests",
    indices = [Index("consumerUserId"), Index("status"), Index("preferredSourceLocation"), Index("milletType")]
)
data class ConsumerMilletRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val consumerUserId: String,
    val milletType: String,
    val quantityKg: Int,
    val neededAtMillis: Long,
    val consumerLocation: String,
    val preferredSourceLocation: String,
    val purpose: String?,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)

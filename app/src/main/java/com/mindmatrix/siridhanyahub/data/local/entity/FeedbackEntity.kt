package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feedback_records",
    indices = [Index("farmerUserId"), Index("consumerUserId"), Index("transactionId")]
)
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: Int,
    val farmerUserId: String,
    val consumerUserId: String,
    val farmerName: String,
    val consumerName: String,
    val milletType: String,
    val quantityKg: Int,
    val rating: Int,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
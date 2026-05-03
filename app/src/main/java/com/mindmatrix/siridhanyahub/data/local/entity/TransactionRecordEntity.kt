package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_records",
    indices = [Index("userId"), Index("role"), Index("transactionType"), Index("milletType")]
)
data class TransactionRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val role: String,
    val transactionType: String,
    val milletType: String,
    val quantityKg: Int,
    val createdAt: Long,
    val counterpartyName: String,
    val referenceContext: String,
    val amountEstimate: Double? = null
)

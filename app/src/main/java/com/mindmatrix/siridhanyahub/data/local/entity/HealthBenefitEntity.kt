package com.mindmatrix.siridhanyahub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_benefits",
    indices = [Index(value = ["milletType"])]
)
data class HealthBenefitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val milletType: String,
    val benefitStatement: String,
    val nutritionalSummary: String?,
    val climateNote: String?,
    val tagline: String
)


package com.mindmatrix.siridhanyahub.data.local.model

data class FarmerInboxItem(
    val matchId: Int,
    val requestId: Int,
    val farmerUserId: String,
    val listingId: Int,
    val matchLocationContext: String,
    val consumerUserId: String,
    val consumerName: String,
    val consumerMobile: String,
    val consumerAddress: String,
    val consumerDistrict: String,
    val milletType: String,
    val quantityKg: Int,
    val neededAtMillis: Long,
    val preferredSourceLocation: String,
    val purpose: String?,
    val status: String
)

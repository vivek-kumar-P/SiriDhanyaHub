package com.mindmatrix.siridhanyahub.data.profile

data class UserProfileDraft(
    val role: UserRole? = null,
    val fullName: String = "",
    val address: String = "",
    val contactNumber: String = "",
    val aadhaarLast4: String = "",
    val pmKisanOrFarmerId: String = "",
    val district: String = "",
    val taluk: String = "",
    val village: String = "",
    val primaryMilletCategory: String = "",
    val landSizeAcres: String = "",
    val preferredPurchaseLocation: String = ""
)

package com.mindmatrix.siridhanyahub.data.profile

data class UserProfileDraft(
    val name: String = "",
    val phone: String = "",
    val district: String = "",
    val role: UserRole? = null,
    val primaryMillet: String = ""
)

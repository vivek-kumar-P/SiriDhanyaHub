package com.mindmatrix.siridhanyahub.data.profile

enum class UserRole(val value: String) {
    FARMER("FARMER"),
    CONSUMER("CONSUMER");

    companion object {
        fun fromValue(value: String?): UserRole? = entries.firstOrNull { it.value == value }
    }
}

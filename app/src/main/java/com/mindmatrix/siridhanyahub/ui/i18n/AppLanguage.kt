package com.mindmatrix.siridhanyahub.ui.i18n

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "EN"),
    KANNADA("kn", "ಕನ್ನಡ"),
    HINDI("hi", "हिन्दी")
}

fun AppLanguage.next(): AppLanguage = when (this) {
    AppLanguage.ENGLISH -> AppLanguage.KANNADA
    AppLanguage.KANNADA -> AppLanguage.HINDI
    AppLanguage.HINDI -> AppLanguage.ENGLISH
}

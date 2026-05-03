package com.mindmatrix.siridhanyahub.ui.i18n

object AppText {
    fun appTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Siri-Dhanya Hub"
        AppLanguage.KANNADA -> "ಸಿರಿ ಧಾನ್ಯ ಹಬ್"
        AppLanguage.HINDI -> "सिरी धान्य हब"
    }

    fun splashTagline(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Millets. Markets. Meals."
        AppLanguage.KANNADA -> "ಸಿರಿಧಾನ್ಯ. ಮಾರುಕಟ್ಟೆ. ಊಟ."
        AppLanguage.HINDI -> "मिलेट्स. बाज़ार. भोजन."
    }

    fun authWelcome(language: AppLanguage, register: Boolean) = when (language) {
        AppLanguage.ENGLISH -> if (register) "Create your account" else "Welcome back"
        AppLanguage.KANNADA -> if (register) "ನಿಮ್ಮ ಖಾತೆ ರಚಿಸಿ" else "ಮತ್ತೆ ಸ್ವಾಗತ"
        AppLanguage.HINDI -> if (register) "अपना खाता बनाएं" else "वापसी पर स्वागत है"
    }

    fun authSubtitle(language: AppLanguage, register: Boolean) = when (language) {
        AppLanguage.ENGLISH ->
            if (register) "Register to start exploring millet prices, recipes, and health facts."
            else "Sign in to continue to Siri-Dhanya Hub."
        AppLanguage.KANNADA ->
            if (register) "ಮಿಲ್ಲೆಟ್ ಬೆಲೆ, ರೆಸಿಪಿ ಮತ್ತು ಆರೋಗ್ಯ ಮಾಹಿತಿಗಾಗಿ ನೋಂದಣಿ ಮಾಡಿ."
            else "ಮುಂದುವರಿಸಲು ಸೈನ್ ಇನ್ ಮಾಡಿ."
        AppLanguage.HINDI ->
            if (register) "मिलेट कीमतें, रेसिपी और स्वास्थ्य जानकारी देखने के लिए पंजीकरण करें।"
            else "आगे बढ़ने के लिए साइन इन करें।"
    }

    fun dashboardGreeting(language: AppLanguage, userName: String) = when (language) {
        AppLanguage.ENGLISH -> "Welcome back, $userName"
        AppLanguage.KANNADA -> "ಮತ್ತೆ ಸ್ವಾಗತ, $userName"
        AppLanguage.HINDI -> "फिर से स्वागत है, $userName"
    }
}

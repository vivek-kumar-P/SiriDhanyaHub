package com.mindmatrix.siridhanyahub.ui.i18n

import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity

object RecipeTranslations {
    data class LocalizedRecipe(
        val title: String,
        val ingredients: String,
        val steps: String
    )

    fun localize(recipe: RecipeEntity, language: AppLanguage): LocalizedRecipe {
        return when (language) {
            AppLanguage.ENGLISH -> LocalizedRecipe(
                title = recipe.nameEnglish,
                ingredients = englishIngredients(recipe.milletType),
                steps = englishSteps(recipe.milletType)
            )
            AppLanguage.KANNADA -> LocalizedRecipe(
                title = kannadaTitle(recipe.nameEnglish),
                ingredients = kannadaIngredients(recipe.milletType),
                steps = kannadaSteps(recipe.milletType)
            )
            AppLanguage.HINDI -> LocalizedRecipe(
                title = hindiTitle(recipe.nameEnglish),
                ingredients = hindiIngredients(recipe.milletType),
                steps = hindiSteps(recipe.milletType)
            )
        }
    }

    private fun englishIngredients(milletType: String) =
        "$milletType, onion, salt, green chilli, cumin, water"

    private fun englishSteps(milletType: String) =
        "1. Wash the $milletType well.\n2. Prepare the masala base and cook the millet.\n3. Simmer until soft and serve warm."

    private fun kannadaIngredients(milletType: String) =
        "$milletType, ಈರುಳ್ಳಿ, ಉಪ್ಪು, ಹಸಿ ಮೆಣಸಿನಕಾಯಿ, ಜೀರಿಗೆ, ನೀರು"

    private fun kannadaSteps(milletType: String) =
        "1. $milletType ಅನ್ನು ಚೆನ್ನಾಗಿ ತೊಳೆಯಿರಿ.\n2. ಮಸಾಲೆ ತಯಾರಿಸಿ ಧಾನ್ಯವನ್ನು ಬೇಯಿಸಿ.\n3. ಮೃದುವಾಗುವವರೆಗೆ ಬೇಯಿಸಿ ಬಿಸಿ ಬಿಸಿ ಸವಿಯಿರಿ."

    private fun hindiIngredients(milletType: String) =
        "$milletType, प्याज, नमक, हरी मिर्च, जीरा, पानी"

    private fun hindiSteps(milletType: String) =
        "1. $milletType को अच्छी तरह धो लें।\n2. मसाला तैयार करके मिलेट पकाएं।\n3. नरम होने तक पकाएं और गरम परोसें।"

    private fun kannadaTitle(english: String) = mapOf(
        "Navane Upma" to "ನವಣೆ ಉಪ್ಮಾ",
        "Navane Mudde" to "ನವಣೆ ಮುದ್ದೆ",
        "Navane Payasa" to "ನವಣೆ ಪಾಯಸ",
        "Sajje Rotti" to "ಸಜ್ಜೆ ರೊಟ್ಟಿ",
        "Sajje Ganji" to "ಸಜ್ಜೆ ಗಂಜಿ",
        "Sajje Unde" to "ಸಜ್ಜೆ ಉಂಡೆ",
        "Baragu Dose" to "ಬರಗು ದೋಸೆ",
        "Baragu Bath" to "ಬರಗು ಬಾತ್",
        "Baragu Khichdi" to "ಬರಗು ಖಿಚ್ಡಿ",
        "Ragi Mudde" to "ರಾಗಿ ಮುದ್ದೆ",
        "Ragi Dose" to "ರಾಗಿ ದೋಸೆ",
        "Ragi Malt" to "ರಾಗಿ ಮಾಲ್ಟ್"
    )[english] ?: english

    private fun hindiTitle(english: String) = mapOf(
        "Navane Upma" to "नवणे उपमा",
        "Navane Mudde" to "नवणे मुद्दे",
        "Navane Payasa" to "नवणे पायसम",
        "Sajje Rotti" to "सज्जे रोटी",
        "Sajje Ganji" to "सज्जे गंजी",
        "Sajje Unde" to "सज्जे उंडे",
        "Baragu Dose" to "बरगु डोसा",
        "Baragu Bath" to "बरगु भात",
        "Baragu Khichdi" to "बरगु खिचड़ी",
        "Ragi Mudde" to "रागी मुद्दे",
        "Ragi Dose" to "रागी डोसा",
        "Ragi Malt" to "रागी माल्ट"
    )[english] ?: english
}

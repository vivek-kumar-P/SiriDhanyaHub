package com.mindmatrix.siridhanyahub.data.seed

import com.mindmatrix.siridhanyahub.data.local.entity.FpoEntity
import com.mindmatrix.siridhanyahub.data.local.entity.HealthBenefitEntity
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import com.mindmatrix.siridhanyahub.data.local.entity.RecipeEntity

object SeedDataProvider {
    private val millets = listOf("Navane", "Sajje", "Baragu", "Ragi")
    private val cities = listOf("Bengaluru", "Davangere", "Mysuru", "Hubli")

    fun priceSeeds(nowMillis: Long = System.currentTimeMillis()): List<PriceEntity> {
        val dayMillis = 24L * 60L * 60L * 1000L
        val basePrices = mapOf(
            "Navane" to 3050.0,
            "Sajje" to 2800.0,
            "Baragu" to 2650.0,
            "Ragi" to 2400.0
        )

        return buildList {
            millets.forEachIndexed { milletIndex, millet ->
                cities.forEachIndexed { cityIndex, city ->
                    repeat(7) { dayOffset ->
                        val price = basePrices.getValue(millet) + (cityIndex * 45) + (dayOffset * 20) - (milletIndex * 30)
                        val previousPrice = price - 15
                        val trend = when {
                            price > previousPrice -> "UP"
                            price < previousPrice -> "DOWN"
                            else -> "NEUTRAL"
                        }
                        add(
                            PriceEntity(
                                milletType = millet,
                                city = city,
                                pricePerQuintal = price,
                                dateRecorded = nowMillis - ((6 - dayOffset) * dayMillis),
                                trendDirection = trend,
                                lastUpdated = nowMillis
                            )
                        )
                    }
                }
            }
        }
    }

    fun recipeSeeds(): List<RecipeEntity> = listOf(
        recipe("ನವಣೆ ಉಪ್ಪಿಟ್ಟು", "Navane Upma", "Navane"),
        recipe("ನವಣೆ ಮುದ್ದೆ", "Navane Mudde", "Navane"),
        recipe("ನವಣೆ ಪಾಯಸ", "Navane Payasa", "Navane"),
        recipe("ಸಜ್ಜೆ ರೊಟ್ಟಿ", "Sajje Rotti", "Sajje"),
        recipe("ಸಜ್ಜೆ ಗಣಜಿ", "Sajje Ganji", "Sajje"),
        recipe("ಸಜ್ಜೆ ಉಂಡೆ", "Sajje Unde", "Sajje"),
        recipe("ಬರಗು ದೋಸೆ", "Baragu Dose", "Baragu"),
        recipe("ಬರಗು ಬಾತ್", "Baragu Bath", "Baragu"),
        recipe("ಬರಗು ಖಿಚ್ಡಿ", "Baragu Khichdi", "Baragu"),
        recipe("ರಾಗಿ ಮುದ್ದೆ", "Ragi Mudde", "Ragi"),
        recipe("ರಾಗಿ ದೋಸೆ", "Ragi Dose", "Ragi"),
        recipe("ರಾಗಿ ಮಾಲ್ಟ್", "Ragi Malt", "Ragi")
    )

    fun healthBenefitSeeds(): List<HealthBenefitEntity> = listOf(
        benefit("Navane", "Supports blood sugar balance.", "Rich in fibre and minerals.", "Uses less water than paddy.", "Good for Diabetics"),
        benefit("Navane", "Helps keep you full for longer.", "Low glycaemic profile.", "Millets are climate-resilient crops.", "Good for Diabetics"),
        benefit("Navane", "Useful in a balanced weight-management diet.", "Contains iron and protein.", "Encourages diverse farming.", "Good for Diabetics"),
        benefit("Sajje", "Supports energy and digestion.", "High in iron and healthy carbs.", "Handles drier climates well.", "Strong Daily Energy"),
        benefit("Sajje", "Can help with satiety.", "Contains fibre and magnesium.", "Needs less irrigation than rice.", "Strong Daily Energy"),
        benefit("Sajje", "Useful for heart-conscious meals.", "Offers useful micronutrients.", "Supports resilient crop cycles.", "Strong Daily Energy"),
        benefit("Baragu", "Suitable for lighter meals.", "Contains fibre and antioxidants.", "Works well in lower-water farming.", "Light and Nourishing"),
        benefit("Baragu", "Can support digestive comfort.", "Balanced grain for everyday meals.", "Good fit for dryland regions.", "Light and Nourishing"),
        benefit("Baragu", "Adds variety to wholesome diets.", "Provides steady energy.", "Encourages crop diversity.", "Light and Nourishing"),
        benefit("Ragi", "Known for calcium content.", "Rich in calcium and fibre.", "Can be cultivated with lower water demand.", "Bone Strength Support"),
        benefit("Ragi", "Good for traditional strength foods.", "Commonly used in nutrient-rich meals.", "Supports sustainable food systems.", "Bone Strength Support"),
        benefit("Ragi", "Useful in filling breakfast options.", "Offers iron and complex carbs.", "Millets reduce pressure on water-intensive crops.", "Bone Strength Support")
    )

    fun fpoSeeds(): List<FpoEntity> = listOf(
        FpoEntity(fpoName = "Davangere Millet Growers FPO", district = "Davangere", address = "PB Road, Davangere", phone = "9876501001", availableMillets = "Navane,Sajje,Ragi", availableQuantityKg = 1200),
        FpoEntity(fpoName = "Bengaluru Rural Organic Farmers FPO", district = "Bengaluru Rural", address = "Devanahalli, Bengaluru Rural", phone = "9876501002", availableMillets = "Navane,Baragu,Ragi", availableQuantityKg = 900),
        FpoEntity(fpoName = "Mysuru Millet Collective", district = "Mysuru", address = "Hunsur Road, Mysuru", phone = "9876501003", availableMillets = "Sajje,Baragu", availableQuantityKg = 700),
        FpoEntity(fpoName = "Hubli Dryland Farmers FPO", district = "Hubballi-Dharwad", address = "Gokul Road, Hubli", phone = "9876501004", availableMillets = "Navane,Sajje", availableQuantityKg = 650),
        FpoEntity(fpoName = "Chitradurga Smart Millet FPO", district = "Chitradurga", address = "Main Market Road, Chitradurga", phone = "9876501005", availableMillets = "Ragi,Baragu,Sajje", availableQuantityKg = 1100)
    )

    fun summary(nowMillis: Long = System.currentTimeMillis()): SeedBundle = SeedBundle(
        prices = priceSeeds(nowMillis).size,
        recipes = recipeSeeds().size,
        healthBenefits = healthBenefitSeeds().size,
        fpos = fpoSeeds().size
    )

    private fun recipe(
        nameKannada: String,
        nameEnglish: String,
        milletType: String
    ): RecipeEntity {
        return RecipeEntity(
            nameKannada = nameKannada,
            nameEnglish = nameEnglish,
            milletType = milletType,
            ingredientsKannada = "ಮಿಲ್ಲೆಟ್, ಈರುಳ್ಳಿ, ಉಪ್ಪು, ಮೆಣಸಿನಕಾಯಿ, ನೀರು",
            stepsKannada = "1. ಪದಾರ್ಥಗಳನ್ನು ತಯಾರಿಸಿ\n2. ಬೆಂಕಿಯಲ್ಲಿ ಬೇಯಿಸಿ\n3. ಸವಿಯಲು ಸಿದ್ಧಪಡಿಸಿ",
            prepTimeMinutes = 25,
            servesCount = 3,
            imageUrl = null
        )
    }

    private fun benefit(
        milletType: String,
        statement: String,
        summary: String,
        climate: String,
        tagline: String
    ): HealthBenefitEntity {
        return HealthBenefitEntity(
            milletType = milletType,
            benefitStatement = statement,
            nutritionalSummary = summary,
            climateNote = climate,
            tagline = tagline
        )
    }
}

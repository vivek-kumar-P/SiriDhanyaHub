package com.mindmatrix.siridhanyahub.data.profile

object KarnatakaProfileData {
    val districts = listOf(
        "Bengaluru Urban",
        "Bengaluru Rural",
        "Mysuru",
        "Mandya",
        "Tumakuru",
        "Chitradurga",
        "Davanagere",
        "Ballari",
        "Hassan",
        "Shivamogga",
        "Hubballi-Dharwad",
        "Belagavi"
    )

    private val taluksByDistrict = mapOf(
        "Bengaluru Urban" to listOf("Bengaluru North", "Bengaluru South", "Anekal"),
        "Bengaluru Rural" to listOf("Devanahalli", "Doddaballapur", "Hosakote"),
        "Mysuru" to listOf("Mysuru", "Nanjangud", "Hunsur"),
        "Mandya" to listOf("Mandya", "Maddur", "Malavalli"),
        "Tumakuru" to listOf("Tumakuru", "Tiptur", "Gubbi"),
        "Chitradurga" to listOf("Chitradurga", "Hiriyur", "Hosadurga"),
        "Davanagere" to listOf("Davanagere", "Harihar", "Honnali"),
        "Ballari" to listOf("Ballari", "Hosapete", "Siruguppa"),
        "Hassan" to listOf("Hassan", "Belur", "Arsikere"),
        "Shivamogga" to listOf("Shivamogga", "Sagara", "Shikaripura"),
        "Hubballi-Dharwad" to listOf("Hubballi", "Dharwad", "Kalghatgi"),
        "Belagavi" to listOf("Belagavi", "Gokak", "Bailhongal")
    )

    private val villagesByTaluk = mapOf(
        "Bengaluru North" to listOf("Yelahanka", "Jakkur", "Hebbal"),
        "Bengaluru South" to listOf("Uttarahalli", "Kengeri", "Banashankari"),
        "Anekal" to listOf("Attibele", "Sarjapura", "Jigani"),
        "Devanahalli" to listOf("Vijayapura", "Kannamangala", "Channarayapatna"),
        "Doddaballapur" to listOf("Kodigehalli", "Bashettihalli", "Tubagere"),
        "Hosakote" to listOf("Nandagudi", "Sulibele", "Anugondanahalli"),
        "Mysuru" to listOf("Srirampura", "Jayapura", "Varuna"),
        "Nanjangud" to listOf("Hullahalli", "Haradanahalli", "Badanavalu"),
        "Hunsur" to listOf("Bilikere", "Hanagodu", "Harave"),
        "Mandya" to listOf("Keregodu", "Keragodu", "Halagur"),
        "Maddur" to listOf("Koppa", "Besagarahalli", "Somanahalli"),
        "Malavalli" to listOf("Bannur", "Halaguru", "Chottanahalli"),
        "Tumakuru" to listOf("Kyathsandra", "Hebbur", "Bugudanahalli"),
        "Tiptur" to listOf("Nonavinakere", "Honnavalli", "Kuppalu"),
        "Gubbi" to listOf("C.S. Pura", "Nittur", "Chelur"),
        "Chitradurga" to listOf("Bharamasagara", "Sirigere", "Aimangala"),
        "Hiriyur" to listOf("Adu Malleshwara", "Dharmapura", "Jagalur Gate"),
        "Hosadurga" to listOf("Bagur", "Kanakuppe", "Madadakere"),
        "Davanagere" to listOf("Mayakonda", "Anagodu", "Tolahunase"),
        "Harihar" to listOf("Malebennur", "Kunibelakere", "Guttur"),
        "Honnali" to listOf("Nyamathi", "Savalanga", "Govinakovi"),
        "Ballari" to listOf("Sanganakallu", "Kurugod", "Allipura"),
        "Hosapete" to listOf("Kamalapura", "Mariyammanahalli", "Hampi"),
        "Siruguppa" to listOf("Tekkalakote", "Karur", "Hatcholli"),
        "Hassan" to listOf("Shantigrama", "Dudda", "Bageshpura"),
        "Belur" to listOf("Bikkod", "Arehalli", "Halebeedu"),
        "Arsikere" to listOf("Banavara", "Gandasi", "Javagal"),
        "Shivamogga" to listOf("Ayanoor", "Savalanga", "Holalur"),
        "Sagara" to listOf("Anandapura", "Kargal", "Avinahalli"),
        "Shikaripura" to listOf("Anjanapura", "Udugani", "Tarikere Camp"),
        "Hubballi" to listOf("Kundgol", "Adargunchi", "Shirur"),
        "Dharwad" to listOf("Garag", "Navalgund", "Mugad"),
        "Kalghatgi" to listOf("Tambur", "Begur", "Bammigatti"),
        "Belagavi" to listOf("Kakati", "Sulebhavi", "Uchagaon"),
        "Gokak" to listOf("Arabhavi", "Konnur", "Nesargi"),
        "Bailhongal" to listOf("Sampagaon", "Murgod", "Kittur")
    )

    val milletTypes = listOf(
        "Ragi",
        "Navane",
        "Sajje",
        "Baragu",
        "Samai",
        "Kodo Millet",
        "Foxtail Millet",
        "Little Millet"
    )

    fun taluksForDistrict(district: String): List<String> = taluksByDistrict[district].orEmpty()

    fun villagesForTaluk(taluk: String): List<String> = villagesByTaluk[taluk].orEmpty()
}

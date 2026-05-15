package com.mindmatrix.siridhanyahub.ui.i18n

object AppText {

    // ── App general ──
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

    fun preparing(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Preparing local app data..."
        AppLanguage.KANNADA -> "ಅಪ್ಲಿಕೇಶನ್ ಡೇಟಾ ತಯಾರಿಸಲಾಗುತ್ತಿದೆ..."
        AppLanguage.HINDI -> "ऐप डेटा तैयार हो रहा है..."
    }

    // ── Auth ──
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

    // ── Guest Home ──
    fun guestTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Siri-Dhanya Hub"
        AppLanguage.KANNADA -> "ಸಿರಿ ಧಾನ್ಯ ಹಬ್"
        AppLanguage.HINDI -> "सिरी धान्य हब"
    }

    fun guestSubtitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Browse first, then login only when you want to become a farmer seller or a real millet buyer."
        AppLanguage.KANNADA -> "ಮೊದಲು ನೋಡಿ, ನಂತರ ರೈತ ಅಥವಾ ಗ್ರಾಹಕರಾಗಲು ಲಾಗಿನ್ ಮಾಡಿ."
        AppLanguage.HINDI -> "पहले ब्राउज़ करें, फिर किसान या उपभोक्ता बनने के लिए लॉगिन करें।"
    }

    fun guestLoginButton(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Login / Signup and complete profile"
        AppLanguage.KANNADA -> "ಲಾಗಿನ್ / ನೋಂದಣಿ ಮಾಡಿ ಮತ್ತು ಪ್ರೊಫೈಲ್ ಪೂರ್ಣಗೊಳಿಸಿ"
        AppLanguage.HINDI -> "लॉगिन / साइनअप करें और प्रोफ़ाइल पूरी करें"
    }

    // ── Farmer Home ──
    fun farmerHomeTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Farmer Home"
        AppLanguage.KANNADA -> "ರೈತ ಮನೆ"
        AppLanguage.HINDI -> "किसान होम"
    }

    fun farmerHomeSubtitle(language: AppLanguage, name: String) = when (language) {
        AppLanguage.ENGLISH -> "Welcome back, $name. Publish stock, watch mandi trends, and track buyer interest."
        AppLanguage.KANNADA -> "ಮತ್ತೆ ಸ್ವಾಗತ, $name. ಸ್ಟಾಕ್ ಪ್ರಕಟಿಸಿ, ಮಂಡಿ ಬೆಲೆ ನೋಡಿ."
        AppLanguage.HINDI -> "फिर से स्वागत है, $name. स्टॉक प्रकाशित करें, मंडी दरें देखें।"
    }

    fun publishFirstStock(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Publish your first stock"
        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಮೊದಲ ಸ್ಟಾಕ್ ಪ್ರಕಟಿಸಿ"
        AppLanguage.HINDI -> "अपना पहला स्टॉक प्रकाशित करें"
    }

    fun updateStockStatus(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Update stock status"
        AppLanguage.KANNADA -> "ಸ್ಟಾಕ್ ಸ್ಥಿತಿ ನವೀಕರಿಸಿ"
        AppLanguage.HINDI -> "स्टॉक स्थिति अपडेट करें"
    }

    fun editFarmerDetails(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Edit farmer details"
        AppLanguage.KANNADA -> "ರೈತ ವಿವರಗಳನ್ನು ಸಂಪಾದಿಸಿ"
        AppLanguage.HINDI -> "किसान विवरण संपादित करें"
    }

    // ── Consumer Home ──
    fun consumerHomeTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Consumer Home"
        AppLanguage.KANNADA -> "ಗ್ರಾಹಕ ಮನೆ"
        AppLanguage.HINDI -> "उपभोक्ता होम"
    }

    fun consumerHomeSubtitle(language: AppLanguage, name: String) = when (language) {
        AppLanguage.ENGLISH -> "Welcome back, $name. Find recipes, learn health value, and request millets from real nearby farmers."
        AppLanguage.KANNADA -> "ಮತ್ತೆ ಸ್ವಾಗತ, $name. ರೆಸಿಪಿ ಹುಡುಕಿ, ಹತ್ತಿರದ ರೈತರಿಂದ ಸಿರಿಧಾನ್ಯ ಖರೀದಿಸಿ."
        AppLanguage.HINDI -> "फिर से स्वागत है, $name. रेसिपी खोजें, पास के किसानों से मिलेट मंगाएं।"
    }

    fun raiseRequest(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Raise a millet request"
        AppLanguage.KANNADA -> "ಸಿರಿಧಾನ್ಯ ವಿನಂತಿ ಸಲ್ಲಿಸಿ"
        AppLanguage.HINDI -> "मिलेट अनुरोध दर्ज करें"
    }

    fun viewActiveRequest(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "View active millet request"
        AppLanguage.KANNADA -> "ಸಕ್ರಿಯ ವಿನಂತಿ ನೋಡಿ"
        AppLanguage.HINDI -> "सक्रिय अनुरोध देखें"
    }

    fun editConsumerDetails(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Edit consumer details"
        AppLanguage.KANNADA -> "ಗ್ರಾಹಕ ವಿವರಗಳನ್ನು ಸಂಪಾದಿಸಿ"
        AppLanguage.HINDI -> "उपभोक्ता विवरण संपादित करें"
    }

    // ── Feature card labels ──
    fun mandiWatch(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Mandi Watch"
        AppLanguage.KANNADA -> "ಮಂಡಿ ವಾಚ್"
        AppLanguage.HINDI -> "मंडी वॉच"
    }

    fun mandiWatchSubGuest(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Browse market rates before signing in"
        AppLanguage.KANNADA -> "ಸೈನ್ ಇನ್ ಮಾಡುವ ಮೊದಲು ಮಾರುಕಟ್ಟೆ ದರ ನೋಡಿ"
        AppLanguage.HINDI -> "साइन इन से पहले बाज़ार दरें देखें"
    }

    fun recipeLab(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Recipe Lab"
        AppLanguage.KANNADA -> "ರೆಸಿಪಿ ಲ್ಯಾಬ್"
        AppLanguage.HINDI -> "रेसिपी लैब"
    }

    fun recipeLabSub(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Explore millet meals and quick cooking ideas"
        AppLanguage.KANNADA -> "ಸಿರಿಧಾನ್ಯ ಊಟ ಮತ್ತು ಅಡುಗೆ ಆಲೋಚನೆಗಳನ್ನು ಅನ್ವೇಷಿಸಿ"
        AppLanguage.HINDI -> "मिलेट भोजन और खाना पकाने के विचार खोजें"
    }

    fun healthBenefits(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Health Benefits"
        AppLanguage.KANNADA -> "ಆರೋಗ್ಯ ಪ್ರಯೋಜನಗಳು"
        AppLanguage.HINDI -> "स्वास्थ्य लाभ"
    }

    fun healthBenefitsSub(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Understand millet nutrition in a friendlier way"
        AppLanguage.KANNADA -> "ಸಿರಿಧಾನ್ಯದ ಪೋಷಣೆಯನ್ನು ಸರಳವಾಗಿ ಅರ್ಥಮಾಡಿ"
        AppLanguage.HINDI -> "मिलेट पोषण को सरल भाषा में समझें"
    }

    fun savedRecipes(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Saved Recipes"
        AppLanguage.KANNADA -> "ಉಳಿಸಿದ ರೆಸಿಪಿಗಳು"
        AppLanguage.HINDI -> "सहेजी गई रेसिपी"
    }

    fun savedRecipesSub(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Open saved ideas whenever you want"
        AppLanguage.KANNADA -> "ಉಳಿಸಿದ ಆಲೋಚನೆಗಳನ್ನು ಯಾವಾಗ ಬೇಕಾದರೂ ತೆರೆಯಿರಿ"
        AppLanguage.HINDI -> "जब चाहें सहेजे गए विचार खोलें"
    }

    fun farmerRequests(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Farmer Requests"
        AppLanguage.KANNADA -> "ರೈತ ವಿನಂತಿಗಳು"
        AppLanguage.HINDI -> "किसान अनुरोध"
    }

    fun farmerRequestsSub(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "See nearby consumer requests and contact them"
        AppLanguage.KANNADA -> "ಹತ್ತಿರದ ಗ್ರಾಹಕ ವಿನಂತಿಗಳನ್ನು ನೋಡಿ"
        AppLanguage.HINDI -> "पास के उपभोक्ता अनुरोध देखें"
    }

    fun buyFromFarmers(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Buy from Real Farmers"
        AppLanguage.KANNADA -> "ನಿಜವಾದ ರೈತರಿಂದ ಖರೀದಿಸಿ"
        AppLanguage.HINDI -> "असली किसानों से खरीदें"
    }

    fun buyFromFarmersSub(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Raise a millet request and let nearby farmers notice it"
        AppLanguage.KANNADA -> "ವಿನಂತಿ ಸಲ್ಲಿಸಿ, ಹತ್ತಿರದ ರೈತರು ನೋಡುತ್ತಾರೆ"
        AppLanguage.HINDI -> "अनुरोध दर्ज करें, पास के किसान देखेंगे"
    }

    fun quickActions(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Quick actions"
        AppLanguage.KANNADA -> "ತ್ವರಿತ ಕ್ರಿಯೆಗಳು"
        AppLanguage.HINDI -> "त्वरित कार्य"
    }

    // ── Settings ──
    fun settingsTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.KANNADA -> "ಸೆಟ್ಟಿಂಗ್‌ಗಳು"
        AppLanguage.HINDI -> "सेटिंग्स"
    }

    fun guestMode(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Guest mode"
        AppLanguage.KANNADA -> "ಅತಿಥಿ ಮೋಡ್"
        AppLanguage.HINDI -> "गेस्ट मोड"
    }

    fun guestModeDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Login to create a farmer or consumer account profile."
        AppLanguage.KANNADA -> "ರೈತ ಅಥವಾ ಗ್ರಾಹಕ ಖಾತೆ ರಚಿಸಲು ಲಾಗಿನ್ ಮಾಡಿ."
        AppLanguage.HINDI -> "किसान या उपभोक्ता खाता बनाने के लिए लॉगिन करें।"
    }

    fun accountProfile(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Account profile"
        AppLanguage.KANNADA -> "ಖಾತೆ ಪ್ರೊಫೈಲ್"
        AppLanguage.HINDI -> "खाता प्रोफ़ाइल"
    }

    fun loginSignup(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Login / Signup"
        AppLanguage.KANNADA -> "ಲಾಗಿನ್ / ನೋಂದಣಿ"
        AppLanguage.HINDI -> "लॉगिन / साइनअप"
    }

    fun accountProfileDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Edit your saved farmer or consumer details."
        AppLanguage.KANNADA -> "ನಿಮ್ಮ ರೈತ ಅಥವಾ ಗ್ರಾಹಕ ವಿವರಗಳನ್ನು ಸಂಪಾದಿಸಿ."
        AppLanguage.HINDI -> "अपने किसान या उपभोक्ता विवरण संपादित करें।"
    }

    fun loginSignupDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Authenticate first, then complete the mandatory role profile."
        AppLanguage.KANNADA -> "ಮೊದಲು ದೃಢೀಕರಿಸಿ, ನಂತರ ಪ್ರೊಫೈಲ್ ಪೂರ್ಣಗೊಳಿಸಿ."
        AppLanguage.HINDI -> "पहले प्रमाणित करें, फिर प्रोफ़ाइल पूरी करें।"
    }

    fun language(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Language"
        AppLanguage.KANNADA -> "ಭಾಷೆ"
        AppLanguage.HINDI -> "भाषा"
    }

    fun languageDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Current: ${language.label}"
        AppLanguage.KANNADA -> "ಪ್ರಸ್ತುತ: ${language.label}"
        AppLanguage.HINDI -> "वर्तमान: ${language.label}"
    }

    fun userAnalytics(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "User analytics"
        AppLanguage.KANNADA -> "ಬಳಕೆದಾರ ವಿಶ್ಲೇಷಣೆ"
        AppLanguage.HINDI -> "उपयोगकर्ता विश्लेषण"
    }

    fun userAnalyticsDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Open your account activity and charts."
        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಖಾತೆ ಚಟುವಟಿಕೆ ಮತ್ತು ಚಾರ್ಟ್ ತೆರೆಯಿರಿ."
        AppLanguage.HINDI -> "अपनी खाता गतिविधि और चार्ट खोलें।"
    }

    fun about(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "About"
        AppLanguage.KANNADA -> "ಬಗ್ಗೆ"
        AppLanguage.HINDI -> "के बारे में"
    }

    fun aboutDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "See the story, purpose, and feature walkthrough of the app."
        AppLanguage.KANNADA -> "ಅಪ್ಲಿಕೇಶನ್‌ನ ಕಥೆ, ಉದ್ದೇಶ ಮತ್ತು ವೈಶಿಷ್ಟ್ಯಗಳನ್ನು ನೋಡಿ."
        AppLanguage.HINDI -> "ऐप की कहानी, उद्देश्य और सुविधाएं देखें।"
    }

    fun logout(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Logout"
        AppLanguage.KANNADA -> "ಲಾಗ್‌ಔಟ್"
        AppLanguage.HINDI -> "लॉगआउट"
    }

    fun logoutDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Sign out and return to browse mode."
        AppLanguage.KANNADA -> "ಸೈನ್ ಔಟ್ ಮಾಡಿ ಮತ್ತು ಬ್ರೌಸ್ ಮೋಡ್‌ಗೆ ಹಿಂತಿರುಗಿ."
        AppLanguage.HINDI -> "साइन आउट करें और ब्राउज़ मोड पर वापस जाएं।"
    }

    fun browseMode(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Browse mode"
        AppLanguage.KANNADA -> "ಬ್ರೌಸ್ ಮೋಡ್"
        AppLanguage.HINDI -> "ब्राउज़ मोड"
    }

    fun browseModeDesc(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "You can still browse Mandi Watch, Recipes, and Health without logging in."
        AppLanguage.KANNADA -> "ಲಾಗಿನ್ ಇಲ್ಲದೆ ಮಂಡಿ, ರೆಸಿಪಿ ಮತ್ತು ಆರೋಗ್ಯ ನೋಡಬಹುದು."
        AppLanguage.HINDI -> "लॉगिन के बिना मंडी, रेसिपी और स्वास्थ्य देख सकते हैं।"
    }

    // ── Mandi Watch ──
    fun mandiWatchTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Mandi Watch"
        AppLanguage.KANNADA -> "ಮಂಡಿ ವಾಚ್"
        AppLanguage.HINDI -> "मंडी वॉच"
    }

    fun mandiWatchHint(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Live millet prices from Karnataka mandis. Select a city to see today's rates."
        AppLanguage.KANNADA -> "ಕರ್ನಾಟಕ ಮಂಡಿಗಳಿಂದ ಲೈವ್ ಸಿರಿಧಾನ್ಯ ಬೆಲೆಗಳು."
        AppLanguage.HINDI -> "कर्नाटक मंडियों से लाइव मिलेट कीमतें।"
    }

    // ── Health Benefits ──
    fun healthTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Health Benefits"
        AppLanguage.KANNADA -> "ಆರೋಗ್ಯ ಪ್ರಯೋಜನಗಳು"
        AppLanguage.HINDI -> "स्वास्थ्य लाभ"
    }

    fun healthHint(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Explore the wellness and nutrition benefits of each millet type."
        AppLanguage.KANNADA -> "ಪ್ರತಿ ಸಿರಿಧಾನ್ಯ ಪ್ರಕಾರದ ಆರೋಗ್ಯ ಪ್ರಯೋಜನಗಳನ್ನು ಅನ್ವೇಷಿಸಿ."
        AppLanguage.HINDI -> "प्रत्येक मिलेट के स्वास्थ्य और पोषण लाभ जानें।"
    }

    // ── Direct Buy ──
    fun directBuyTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Buy millets from real farmers"
        AppLanguage.KANNADA -> "ನಿಜವಾದ ರೈತರಿಂದ ಸಿರಿಧಾನ್ಯ ಖರೀದಿಸಿ"
        AppLanguage.HINDI -> "असली किसानों से मिलेट खरीदें"
    }

    fun availableFarmers(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Available Farmers"
        AppLanguage.KANNADA -> "ಲಭ್ಯವಿರುವ ರೈತರು"
        AppLanguage.HINDI -> "उपलब्ध किसान"
    }

    fun noFarmersYet(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "No farmers have published stock yet. Check back soon."
        AppLanguage.KANNADA -> "ಇನ್ನೂ ಯಾವ ರೈತರೂ ಸ್ಟಾಕ್ ಪ್ರಕಟಿಸಿಲ್ಲ. ಶೀಘ್ರದಲ್ಲೇ ಮತ್ತೆ ಪರಿಶೀಲಿಸಿ."
        AppLanguage.HINDI -> "अभी तक किसी किसान ने स्टॉक प्रकाशित नहीं किया। जल्द जांचें।"
    }

    fun raiseMilletRequest(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Raise a Millet Request"
        AppLanguage.KANNADA -> "ಸಿರಿಧಾನ್ಯ ವಿನಂತಿ ಸಲ್ಲಿಸಿ"
        AppLanguage.HINDI -> "मिलेट अनुरोध दर्ज करें"
    }

    fun directBuyHint(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Raise one active request. Nearby farmers with live stock will see it."
        AppLanguage.KANNADA -> "ಒಂದು ಸಕ್ರಿಯ ವಿನಂತಿ ಸಲ್ಲಿಸಿ. ಹತ್ತಿರದ ರೈತರು ನೋಡುತ್ತಾರೆ."
        AppLanguage.HINDI -> "एक सक्रिय अनुरोध दर्ज करें। पास के किसान देखेंगे।"
    }

    fun saveRequest(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Save request"
        AppLanguage.KANNADA -> "ವಿನಂತಿ ಉಳಿಸಿ"
        AppLanguage.HINDI -> "अनुरोध सहेजें"
    }

    fun saving(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Saving..."
        AppLanguage.KANNADA -> "ಉಳಿಸಲಾಗುತ್ತಿದೆ..."
        AppLanguage.HINDI -> "सहेजा जा रहा है..."
    }

    fun markFulfilled(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Mark fulfilled"
        AppLanguage.KANNADA -> "ಪೂರ್ಣಗೊಂಡಿದೆ ಎಂದು ಗುರುತಿಸಿ"
        AppLanguage.HINDI -> "पूर्ण के रूप में चिह्नित करें"
    }

    fun deleteRequest(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Delete"
        AppLanguage.KANNADA -> "ಅಳಿಸಿ"
        AppLanguage.HINDI -> "हटाएं"
    }

    fun activeRequest(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Active request"
        AppLanguage.KANNADA -> "ಸಕ್ರಿಯ ವಿನಂತಿ"
        AppLanguage.HINDI -> "सक्रिय अनुरोध"
    }

    fun consumerOnlyPage(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "This page is only for consumer accounts."
        AppLanguage.KANNADA -> "ಈ ಪುಟ ಕೇವಲ ಗ್ರಾಹಕ ಖಾತೆಗಳಿಗೆ ಮಾತ್ರ."
        AppLanguage.HINDI -> "यह पृष्ठ केवल उपभोक्ता खातों के लिए है।"
    }

    // ── Farmer Requests ──
    fun consumerRequestsTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Consumer Requests"
        AppLanguage.KANNADA -> "ಗ್ರಾಹಕ ವಿನಂತಿಗಳು"
        AppLanguage.HINDI -> "उपभोक्ता अनुरोध"
    }

    fun consumerRequestsHint(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "These are active millet requests from consumers. Contact them directly using the mobile number shown."
        AppLanguage.KANNADA -> "ಇವು ಗ್ರಾಹಕರ ಸಕ್ರಿಯ ಸಿರಿಧಾನ್ಯ ವಿನಂತಿಗಳಾಗಿವೆ. ತೋರಿಸಿದ ಮೊಬೈಲ್ ನಂಬರ್ ಮೂಲಕ ಸಂಪರ್ಕಿಸಿ."
        AppLanguage.HINDI -> "ये उपभोक्ताओं के सक्रिय मिलेट अनुरोध हैं। दिखाए गए मोबाइल नंबर से सीधे संपर्क करें।"
    }

    fun noConsumerRequestsYet(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "No consumer requests yet. Check back soon."
        AppLanguage.KANNADA -> "ಇನ್ನೂ ಯಾವ ಗ್ರಾಹಕ ವಿನಂತಿಗಳಿಲ್ಲ. ಶೀಘ್ರದಲ್ಲೇ ಮತ್ತೆ ಪರಿಶೀಲಿಸಿ."
        AppLanguage.HINDI -> "अभी तक कोई उपभोक्ता अनुरोध नहीं। जल्द जांचें।"
    }

    fun farmerOnlyPage(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Only farmer accounts can view this page."
        AppLanguage.KANNADA -> "ಈ ಪುಟವನ್ನು ಕೇವಲ ರೈತ ಖಾತೆಗಳು ಮಾತ್ರ ನೋಡಬಹುದು."
        AppLanguage.HINDI -> "यह पृष्ठ केवल किसान खाते देख सकते हैं।"
    }

    // ── Health Benefits ──
    fun healthPageHint(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Each millet gets its own wellness card. Tap to explore nutrition and health facts."
        AppLanguage.KANNADA -> "ಪ್ರತಿ ಸಿರಿಧಾನ್ಯಕ್ಕೆ ತನ್ನದೇ ಆರೋಗ್ಯ ಕಾರ್ಡ್ ಇದೆ. ಪೋಷಣೆ ಮತ್ತು ಆರೋಗ್ಯ ಮಾಹಿತಿ ನೋಡಲು ತಟ್ಟಿರಿ."
        AppLanguage.HINDI -> "हर मिलेट का अपना वेलनेस कार्ड है। पोषण और स्वास्थ्य जानकारी देखने के लिए टैप करें।"
    }

    fun healthTapPrompt(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Tap to see full nutrition and wellness details"
        AppLanguage.KANNADA -> "ಪೂರ್ಣ ಪೋಷಣೆ ಮತ್ತು ಆರೋಗ್ಯ ವಿವರಗಳನ್ನು ನೋಡಲು ತಟ್ಟಿರಿ"
        AppLanguage.HINDI -> "पूरी पोषण और स्वास्थ्य जानकारी देखने के लिए टैप करें"
    }

    fun healthBenefitsLabel(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Health Benefits"
        AppLanguage.KANNADA -> "ಆರೋಗ್ಯ ಲಾಭಗಳು"
        AppLanguage.HINDI -> "स्वास्थ्य लाभ"
    }

    fun nutritionSummaryLabel(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Nutrition Summary"
        AppLanguage.KANNADA -> "ಪೋಷಕಾಂಶ ಸಾರಾಂಶ"
        AppLanguage.HINDI -> "पोषण सारांश"
    }

    fun climateNoteLabel(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Climate Action Note"
        AppLanguage.KANNADA -> "ಹವಾಮಾನ ಕ್ರಮ ಟಿಪ್ಪಣಿ"
        AppLanguage.HINDI -> "जलवायु नोट"
    }

    // ── Mandi Watch ──
    fun mandiWatchPageTitle(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Mandi Watch"
        AppLanguage.KANNADA -> "ಮಂಡಿ ವಾಚ್"
        AppLanguage.HINDI -> "मंडी वॉच"
    }

    fun mandiWatchFarmerHint(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Select a city, choose your preferred mandi and rate, then publish your live stock."
        AppLanguage.KANNADA -> "ನಗರ ಆಯ್ಕೆ ಮಾಡಿ, ಮಂಡಿ ಮತ್ತು ದರ ಆಯ್ಕೆ ಮಾಡಿ, ನಂತರ ನಿಮ್ಮ ಸ್ಟಾಕ್ ಪ್ರಕಟಿಸಿ."
        AppLanguage.HINDI -> "शहर चुनें, मंडी और दर चुनें, फिर अपना स्टॉक प्रकाशित करें।"
    }

    fun mandiOnlyFarmer(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "This page is only for farmer accounts."
        AppLanguage.KANNADA -> "ಈ ಪುಟ ಕೇವಲ ರೈತ ಖಾತೆಗಳಿಗೆ ಮಾತ್ರ."
        AppLanguage.HINDI -> "यह पृष्ठ केवल किसान खातों के लिए है।"
    }

    fun searchCityMillet(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Search city or millet"
        AppLanguage.KANNADA -> "ನಗರ ಅಥವಾ ಸಿರಿಧಾನ್ಯ ಹುಡುಕಿ"
        AppLanguage.HINDI -> "शहर या मिलेट खोजें"
    }

    fun pricesAcrossKarnataka(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Prices across Karnataka markets"
        AppLanguage.KANNADA -> "ಕರ್ನಾಟಕ ಮಾರುಕಟ್ಟೆಗಳಲ್ಲಿ ಬೆಲೆಗಳು"
        AppLanguage.HINDI -> "कर्नाटक मंडियों में कीमतें"
    }

    fun chooseMandiRate(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Choose your preferred mandi and rate"
        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಮಂಡಿ ಮತ್ತು ದರ ಆಯ್ಕೆ ಮಾಡಿ"
        AppLanguage.HINDI -> "अपनी मंडी और दर चुनें"
    }

    fun availableMilletStock(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Your available millet stock"
        AppLanguage.KANNADA -> "ನಿಮ್ಮ ಲಭ್ಯವಿರುವ ಸಿರಿಧಾನ್ಯ ಸ್ಟಾಕ್"
        AppLanguage.HINDI -> "आपका उपलब्ध मिलेट स्टॉक"
    }

    fun reviewAndConfirm(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Review and confirm"
        AppLanguage.KANNADA -> "ಪರಿಶೀಲಿಸಿ ಮತ್ತು ದೃಢೀಕರಿಸಿ"
        AppLanguage.HINDI -> "समीक्षा करें और पुष्टि करें"
    }

    fun publishing(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Publishing..."
        AppLanguage.KANNADA -> "ಪ್ರಕಟಿಸಲಾಗುತ್ತಿದೆ..."
        AppLanguage.HINDI -> "प्रकाशित हो रहा है..."
    }

    fun confirmStock(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Confirm stock availability"
        AppLanguage.KANNADA -> "ಸ್ಟಾಕ್ ಲಭ್ಯತೆ ದೃಢೀಕರಿಸಿ"
        AppLanguage.HINDI -> "स्टॉक उपलब्धता की पुष्टि करें"
    }

    fun reselect(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Reselect"
        AppLanguage.KANNADA -> "ಮರು ಆಯ್ಕೆ ಮಾಡಿ"
        AppLanguage.HINDI -> "फिर से चुनें"
    }

    fun tapToSelect(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Tap to select"
        AppLanguage.KANNADA -> "ಆಯ್ಕೆ ಮಾಡಲು ತಟ್ಟಿರಿ"
        AppLanguage.HINDI -> "चुनने के लिए टैप करें"
    }

    fun selected(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "✓ Selected"
        AppLanguage.KANNADA -> "✓ ಆಯ್ಕೆಯಾಗಿದೆ"
        AppLanguage.HINDI -> "✓ चुना गया"
    }
}
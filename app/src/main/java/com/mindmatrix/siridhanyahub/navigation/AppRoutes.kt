package com.mindmatrix.siridhanyahub.navigation

import android.net.Uri

sealed class AppRoute(val route: String) {
    data object Home : AppRoute("home")
    data object FarmerHome : AppRoute("farmerHome")
    data object ConsumerHome : AppRoute("consumerHome")
    data object Settings : AppRoute("settings")
    data object Auth : AppRoute("auth?redirect={redirect}") {
        fun createRoute(redirect: String? = null): String {
            return if (redirect.isNullOrBlank()) {
                "auth"
            } else {
                "auth?redirect=${Uri.encode(redirect)}"
            }
        }
    }

    data object ProfileSetup : AppRoute("profileSetup")

    data object Mandi : AppRoute("mandi")
    data object Recipes : AppRoute("recipes")
    data object Health : AppRoute("health")
    data object Buy : AppRoute("buy")
    data object Saved : AppRoute("saved")
    data object FarmerRequests : AppRoute("farmerRequests")
    data object About : AppRoute("about")
    data object Analytics : AppRoute("analytics")
}

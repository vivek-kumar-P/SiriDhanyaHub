package com.mindmatrix.siridhanyahub.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.screens.AboutScreen
import com.mindmatrix.siridhanyahub.ui.screens.AnalyticsScreen
import com.mindmatrix.siridhanyahub.ui.screens.AuthScreen
import com.mindmatrix.siridhanyahub.ui.screens.ConsumerHomeScreen
import com.mindmatrix.siridhanyahub.ui.screens.DirectBuyScreen
import com.mindmatrix.siridhanyahub.ui.screens.FarmerHomeScreen
import com.mindmatrix.siridhanyahub.ui.screens.FarmerRequestsScreen
import com.mindmatrix.siridhanyahub.ui.screens.FavouritesScreen
import com.mindmatrix.siridhanyahub.ui.screens.GuestHomeScreen
import com.mindmatrix.siridhanyahub.ui.screens.HealthBenefitsScreen
import com.mindmatrix.siridhanyahub.ui.screens.MandiWatchScreen
import com.mindmatrix.siridhanyahub.ui.screens.ProfileSetupScreen
import com.mindmatrix.siridhanyahub.ui.screens.RecipeLabScreen
import com.mindmatrix.siridhanyahub.ui.screens.SettingsScreen
import com.mindmatrix.siridhanyahub.viewmodel.AuthViewModel

@Composable
fun SiriDhanyaNavHost(
    navController: NavHostController,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    fun protectedNavigate(route: String) {
        if (authState.user == null) {
            navController.navigate(AppRoute.Auth.createRoute(route))
        } else {
            navController.navigate(route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it / 3 }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it / 4 }) + fadeOut()
        }
    ) {
        composable(AppRoute.Home.route) {
            GuestHomeScreen(
                onSettings = { navController.navigate(AppRoute.Settings.route) },
                onMandi = { navController.navigate(AppRoute.Mandi.route) },
                onRecipes = { navController.navigate(AppRoute.Recipes.route) },
                onHealth = { navController.navigate(AppRoute.Health.route) },
                onSaved = { navController.navigate(AppRoute.Saved.route) },
                onProfileSetup = { navController.navigate(AppRoute.Auth.createRoute(AppRoute.ProfileSetup.route)) }
            )
        }

        composable(AppRoute.FarmerHome.route) {
            FarmerHomeScreen(
                onSettings = { navController.navigate(AppRoute.Settings.route) },
                onMandi = { navController.navigate(AppRoute.Mandi.route) },
                onRecipes = { navController.navigate(AppRoute.Recipes.route) },
                onHealth = { navController.navigate(AppRoute.Health.route) },
                onFarmerRequests = { protectedNavigate(AppRoute.FarmerRequests.route) },
                onProfileSetup = { navController.navigate(AppRoute.ProfileSetup.route) }
            )
        }

        composable(AppRoute.ConsumerHome.route) {
            ConsumerHomeScreen(
                onSettings = { navController.navigate(AppRoute.Settings.route) },
                onBuy = { protectedNavigate(AppRoute.Buy.route) },
                onRecipes = { navController.navigate(AppRoute.Recipes.route) },
                onHealth = { navController.navigate(AppRoute.Health.route) },
                onSaved = { navController.navigate(AppRoute.Saved.route) },
                onProfileSetup = { navController.navigate(AppRoute.ProfileSetup.route) }
            )
        }

        composable(AppRoute.Settings.route) {
            SettingsScreen(
                language = language,
                onLanguageChange = onLanguageChange,
                onBack = { navController.popBackStack() },
                onAuth = { navController.navigate(AppRoute.Auth.createRoute(AppRoute.ProfileSetup.route)) },
                onEditProfile = { navController.navigate(AppRoute.ProfileSetup.route) },
                onAnalytics = { protectedNavigate(AppRoute.Analytics.route) },
                onAbout = { navController.navigate(AppRoute.About.route) }
            )
        }

        composable(
            route = AppRoute.Auth.route,
            arguments = listOf(
                navArgument("redirect") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect")
            AuthScreen(
                language = language,
                onBack = { navController.popBackStack() },
                onEnterApp = { resolvedRoute ->
                    val nextRoute = if (resolvedRoute == AppRoute.ProfileSetup.route) {
                        resolvedRoute
                    } else {
                        redirect ?: resolvedRoute
                    }
                    if (!nextRoute.isNullOrBlank()) {
                        navController.navigate(Uri.decode(nextRoute)) {
                            popUpTo(AppRoute.Auth.route) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(AppRoute.ProfileSetup.route) {
            ProfileSetupScreen(
                onBack = { navController.popBackStack() },
                onAuthRequired = { navController.navigate(AppRoute.Auth.createRoute(AppRoute.ProfileSetup.route)) },
                onSaved = { isFarmer ->
                    navController.navigate(if (isFarmer) AppRoute.FarmerHome.route else AppRoute.ConsumerHome.route) {
                        popUpTo(AppRoute.ProfileSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.Mandi.route) {
            MandiWatchScreen(
                language = language,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Recipes.route) {
            RecipeLabScreen(
                language = language,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Health.route) {
            HealthBenefitsScreen(
                language = language,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Buy.route) {
            DirectBuyScreen(
                language = language,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Saved.route) {
            FavouritesScreen(
                language = language,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.FarmerRequests.route) {
            FarmerRequestsScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable(AppRoute.Analytics.route) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }
    }
}

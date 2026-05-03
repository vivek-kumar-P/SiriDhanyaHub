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
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.screens.AnalyticsScreen
import com.mindmatrix.siridhanyahub.ui.screens.AuthScreen
import com.mindmatrix.siridhanyahub.ui.screens.DirectBuyScreen
import com.mindmatrix.siridhanyahub.ui.screens.FavouritesScreen
import com.mindmatrix.siridhanyahub.ui.screens.HealthBenefitsScreen
import com.mindmatrix.siridhanyahub.ui.screens.HomeDashboardScreen
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
            HomeDashboardScreen(
                onSettings = { navController.navigate(AppRoute.Settings.route) },
                onMandi = { navController.navigate(AppRoute.Mandi.route) },
                onRecipes = { navController.navigate(AppRoute.Recipes.route) },
                onHealth = { navController.navigate(AppRoute.Health.route) },
                onBuy = { protectedNavigate(AppRoute.Buy.route) },
                onSaved = { protectedNavigate(AppRoute.Saved.route) },
                onRoleSetup = { role -> navController.navigate(AppRoute.ProfileSetup.createRoute(role)) }
            )
        }

        composable(AppRoute.Settings.route) {
            SettingsScreen(
                language = language,
                onLanguageChange = onLanguageChange,
                onBack = { navController.popBackStack() },
                onAuth = { navController.navigate(AppRoute.Auth.createRoute()) },
                onAnalytics = { protectedNavigate(AppRoute.Analytics.route) }
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
                onEnterApp = {
                    if (!redirect.isNullOrBlank()) {
                        navController.navigate(Uri.decode(redirect)) {
                            popUpTo(AppRoute.Auth.route) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = AppRoute.ProfileSetup.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = UserRole.fromValue(backStackEntry.arguments?.getString("role")) ?: UserRole.CONSUMER
            ProfileSetupScreen(
                role = role,
                onBack = { navController.popBackStack() },
                onAuthRequired = { navController.navigate(AppRoute.Auth.createRoute(AppRoute.ProfileSetup.createRoute(role))) },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Mandi.route) {
            MandiWatchScreen(language = language)
        }

        composable(AppRoute.Recipes.route) {
            RecipeLabScreen(language = language)
        }

        composable(AppRoute.Health.route) {
            HealthBenefitsScreen(language = language)
        }

        composable(AppRoute.Buy.route) {
            DirectBuyScreen(language = language)
        }

        composable(AppRoute.Saved.route) {
            FavouritesScreen(language = language)
        }

        composable(AppRoute.Analytics.route) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }
    }
}

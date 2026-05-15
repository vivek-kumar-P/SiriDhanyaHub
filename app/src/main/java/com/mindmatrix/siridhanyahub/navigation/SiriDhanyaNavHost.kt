package com.mindmatrix.siridhanyahub.navigation

import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.mindmatrix.siridhanyahub.ui.screens.AboutScreen
import com.mindmatrix.siridhanyahub.ui.screens.AnalyticsScreen
import com.mindmatrix.siridhanyahub.ui.screens.AuthScreen
import com.mindmatrix.siridhanyahub.ui.screens.ConsumerHomeScreen
import com.mindmatrix.siridhanyahub.ui.screens.ConsumerProfileFormScreen
import com.mindmatrix.siridhanyahub.ui.screens.DirectBuyScreen
import com.mindmatrix.siridhanyahub.ui.screens.FarmerHomeScreen
import com.mindmatrix.siridhanyahub.ui.screens.FarmerProfileFormScreen
import com.mindmatrix.siridhanyahub.ui.screens.FarmerRequestsScreen
import com.mindmatrix.siridhanyahub.ui.screens.FavouritesScreen
import com.mindmatrix.siridhanyahub.ui.screens.GuestHomeScreen
import com.mindmatrix.siridhanyahub.ui.screens.HealthBenefitsScreen
import com.mindmatrix.siridhanyahub.ui.screens.MandiWatchScreen
import com.mindmatrix.siridhanyahub.ui.screens.RecipeLabScreen
import com.mindmatrix.siridhanyahub.ui.screens.RoleConfirmationScreen
import com.mindmatrix.siridhanyahub.ui.screens.SettingsScreen
import com.mindmatrix.siridhanyahub.ui.screens.SplashScreen
import com.mindmatrix.siridhanyahub.viewmodel.AuthViewModel
import com.mindmatrix.siridhanyahub.viewmodel.ProfileSetupViewModel

@Composable
fun SiriDhanyaNavHost(
    navController: NavHostController,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val profileViewModel: ProfileSetupViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

    fun protectedNavigate(route: String) {
        if (authState.user == null) {
            navController.navigate(AppRoute.Auth.createRoute(route))
        } else {
            navController.navigate(route)
        }
    }

    fun profileEntryRoute(): String {
        return when {
            authState.user == null -> AppRoute.RoleConfirmation.route
            profileState.activeRole == UserRole.FARMER -> AppRoute.FarmerProfile.route
            profileState.activeRole == UserRole.CONSUMER -> AppRoute.ConsumerProfile.route
            else -> AppRoute.RoleConfirmation.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Launch.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { it / 3 }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 4 }) + fadeOut() }
    ) {
        composable(AppRoute.Launch.route) {
            SplashScreen(
                language = language,
                onContinue = { route ->
                    navController.navigate(route) {
                        popUpTo(AppRoute.Launch.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.Home.route) {
            GuestHomeScreen(
                language = language,
                onSettings = { navController.navigate(AppRoute.Settings.route) },
                onMandi = { navController.navigate(AppRoute.Mandi.route) },
                onRecipes = { navController.navigate(AppRoute.Recipes.route) },
                onHealth = { navController.navigate(AppRoute.Health.route) },
                onSaved = { navController.navigate(AppRoute.Saved.route) },
                onProfileSetup = { navController.navigate(AppRoute.ProfileSetup.route) }
            )
        }
        composable(AppRoute.FarmerHome.route) {
            FarmerHomeScreen(
                language = language,
                onSettings = { navController.navigate(AppRoute.Settings.route) },
                onMandi = { navController.navigate(AppRoute.Mandi.route) },
                onRecipes = { navController.navigate(AppRoute.Recipes.route) },
                onHealth = { navController.navigate(AppRoute.Health.route) },
                onFarmerRequests = { protectedNavigate(AppRoute.FarmerRequests.route) },
                onProfileSetup = { navController.navigate(AppRoute.FarmerProfile.route) }
            )
        }
        composable(AppRoute.ConsumerHome.route) {
            ConsumerHomeScreen(
                language = language,
                onSettings = { navController.navigate(AppRoute.Settings.route) },
                onBuy = { protectedNavigate(AppRoute.Buy.route) },
                onRecipes = { navController.navigate(AppRoute.Recipes.route) },
                onHealth = { navController.navigate(AppRoute.Health.route) },
                onSaved = { navController.navigate(AppRoute.Saved.route) },
                onProfileSetup = { navController.navigate(AppRoute.ConsumerProfile.route) }
            )
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                language = language,
                onLanguageChange = onLanguageChange,
                onBack = { navController.popBackStack() },
                onAuth = { navController.navigate(AppRoute.ProfileSetup.route) },
                onEditProfile = { navController.navigate(profileEntryRoute()) },
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
                },
                navArgument("register") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect")
            val register = backStackEntry.arguments?.getBoolean("register") ?: false
            AuthScreen(
                language = language,
                initialRegisterMode = register,
                onBack = {
                    if (register && authState.user == null) {
                        profileViewModel.clearPendingOnboardingRole()
                    }
                    navController.popBackStack()
                },
                onEnterApp = { resolvedRoute ->
                    val nextRoute = redirect ?: resolvedRoute
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
            val nextRoute = when {
                authState.user == null -> AppRoute.RoleConfirmation.route
                profileState.activeRole == UserRole.FARMER -> AppRoute.FarmerProfile.route
                profileState.activeRole == UserRole.CONSUMER -> AppRoute.ConsumerProfile.route
                else -> AppRoute.RoleConfirmation.route
            }
            androidx.compose.runtime.LaunchedEffect(nextRoute) {
                navController.navigate(nextRoute) {
                    popUpTo(AppRoute.ProfileSetup.route) { inclusive = true }
                }
            }
        }
        composable(AppRoute.RoleConfirmation.route) {
            RoleConfirmationScreen(
                onBack = { navController.popBackStack() },
                onAuthRequired = { role ->
                    profileViewModel.startOnboarding(role)
                    val profileRoute = if (role == UserRole.FARMER) {
                        AppRoute.FarmerProfile.route
                    } else {
                        AppRoute.ConsumerProfile.route
                    }
                    navController.navigate(AppRoute.Auth.createRoute(profileRoute, register = true))
                },
                onConfirmed = { role ->
                    navController.navigate(if (role == UserRole.FARMER) AppRoute.FarmerProfile.route else AppRoute.ConsumerProfile.route) {
                        popUpTo(AppRoute.RoleConfirmation.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.FarmerProfile.route) {
            FarmerProfileFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(AppRoute.FarmerHome.route) {
                        popUpTo(AppRoute.FarmerProfile.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.ConsumerProfile.route) {
            ConsumerProfileFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(AppRoute.ConsumerHome.route) {
                        popUpTo(AppRoute.ConsumerProfile.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.Mandi.route) {
            MandiWatchScreen(language = language, onBack = { navController.popBackStack() })
        }
        composable(AppRoute.Recipes.route) {
            RecipeLabScreen(language = language, onBack = { navController.popBackStack() })
        }
        composable(AppRoute.Health.route) {
            HealthBenefitsScreen(language = language, onBack = { navController.popBackStack() })
        }
        composable(AppRoute.Buy.route) {
            DirectBuyScreen(language = language, onBack = { navController.popBackStack() })
        }
        composable(AppRoute.Saved.route) {
            FavouritesScreen(language = language, onBack = { navController.popBackStack() })
        }
        composable(AppRoute.FarmerRequests.route) {
            FarmerRequestsScreen(
                language = language,
                onBack = { navController.popBackStack() })
        }
        composable(AppRoute.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoute.Analytics.route) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }
    }
}

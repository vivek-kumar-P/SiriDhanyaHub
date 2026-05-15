package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.local.entity.ConsumerMilletRequestEntity
import com.mindmatrix.siridhanyahub.ui.components.FeatureCard
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.AppText
import com.mindmatrix.siridhanyahub.ui.theme.ForestGreen
import com.mindmatrix.siridhanyahub.ui.theme.HarvestAmber
import com.mindmatrix.siridhanyahub.ui.theme.SoilGrey
import com.mindmatrix.siridhanyahub.ui.theme.TerracottaRed
import com.mindmatrix.siridhanyahub.viewmodel.DirectBuyViewModel
import com.mindmatrix.siridhanyahub.viewmodel.HomeViewModel

data class HomeFeature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHomeScreen(
    language: AppLanguage,
    onSettings: () -> Unit,
    onMandi: () -> Unit,
    onRecipes: () -> Unit,
    onHealth: () -> Unit,
    onSaved: () -> Unit,
    onProfileSetup: () -> Unit
) {
    val features = listOf(
        HomeFeature(AppText.mandiWatch(language), AppText.mandiWatchSubGuest(language), Icons.AutoMirrored.Filled.ShowChart, ForestGreen, onMandi),
        HomeFeature(AppText.recipeLab(language), AppText.recipeLabSub(language), Icons.Default.LocalFlorist, HarvestAmber, onRecipes),
        HomeFeature(AppText.healthBenefits(language), AppText.healthBenefitsSub(language), Icons.Default.MedicalServices, TerracottaRed, onHealth),
        HomeFeature(AppText.savedRecipes(language), AppText.savedRecipesSub(language), Icons.AutoMirrored.Filled.Assignment, SoilGrey, onSaved)
    )
    BaseHomeScreen(
        title = AppText.guestTitle(language),
        subtitle = AppText.guestSubtitle(language),
        chip = null,
        features = features,
        onSettings = onSettings,
        primaryAction = {
            Button(onClick = onProfileSetup, modifier = Modifier.fillMaxWidth()) {
                Text(AppText.guestLoginButton(language))
            }
        },
        quickActionsLabel = AppText.quickActions(language)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
    language: AppLanguage,
    onSettings: () -> Unit,
    onMandi: () -> Unit,
    onRecipes: () -> Unit,
    onHealth: () -> Unit,
    onFarmerRequests: () -> Unit,
    onProfileSetup: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listing = state.farmerListing
    val features = listOf(
        HomeFeature(AppText.mandiWatch(language), AppText.mandiWatchTitle(language), Icons.AutoMirrored.Filled.ShowChart, ForestGreen, onMandi),
        HomeFeature(AppText.farmerRequests(language), AppText.farmerRequestsSub(language), Icons.Default.NotificationsActive, TerracottaRed, onFarmerRequests),
        HomeFeature(AppText.recipeLab(language), AppText.recipeLabSub(language), Icons.Default.LocalFlorist, HarvestAmber, onRecipes),
        HomeFeature(AppText.healthBenefits(language), AppText.healthBenefitsSub(language), Icons.Default.MedicalServices, SoilGrey, onHealth)
    )
    BaseHomeScreen(
        title = AppText.farmerHomeTitle(language),
        subtitle = AppText.farmerHomeSubtitle(language, state.activeProfile?.fullName.orEmpty()),
        chip = when (state.activeProfile?.stockStatus) {
            "PENDING_VERIFICATION" -> "⏳ Account pending admin verification"
            "STOCK_AVAILABLE" -> "✓ Stock published and visible"
            "READY_FOR_STOCK" -> AppText.publishFirstStock(language)
            else -> "Profile active"
        },
        features = features,
        onSettings = onSettings,
        primaryAction = {
            if (listing != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Current stock is visible from ${listing.marketPlace}, ${listing.marketCity} at Rs.${listing.selectedMarketRate.toInt()} per quintal.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(onClick = onMandi) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Text(AppText.updateStockStatus(language), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            } else {
                Button(onClick = onMandi, modifier = Modifier.fillMaxWidth()) {
                    Text(AppText.publishFirstStock(language))
                }
            }
        },
        secondaryAction = {
            OutlinedButton(onClick = onProfileSetup, modifier = Modifier.fillMaxWidth()) {
                Text(AppText.editFarmerDetails(language))
            }
        },
        quickActionsLabel = AppText.quickActions(language)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerHomeScreen(
    language: AppLanguage,
    onSettings: () -> Unit,
    onBuy: () -> Unit,
    onRecipes: () -> Unit,
    onHealth: () -> Unit,
    onSaved: () -> Unit,
    onProfileSetup: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    requestViewModel: DirectBuyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activeRequest = state.activeConsumerRequest
    var showActiveRequestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn, activeRequest?.id) {
        showActiveRequestDialog = state.isLoggedIn && activeRequest != null
    }

    if (showActiveRequestDialog && activeRequest != null) {
        ActiveRequestDialog(
            language = language,
            request = activeRequest,
            onDismiss = { showActiveRequestDialog = false },
            onViewRequest = {
                showActiveRequestDialog = false
                onBuy()
            },
            onMarkFulfilled = {
                requestViewModel.markFulfilled()
                showActiveRequestDialog = false
            },
            onDelete = {
                requestViewModel.deleteRequest()
                showActiveRequestDialog = false
            }
        )
    }

    val features = listOf(
        HomeFeature(AppText.buyFromFarmers(language), AppText.buyFromFarmersSub(language), Icons.Default.ShoppingCart, ForestGreen, onBuy),
        HomeFeature(AppText.recipeLab(language), AppText.recipeLabSub(language), Icons.Default.LocalFlorist, HarvestAmber, onRecipes),
        HomeFeature(AppText.healthBenefits(language), AppText.healthBenefitsSub(language), Icons.Default.MedicalServices, TerracottaRed, onHealth),
        HomeFeature(AppText.savedRecipes(language), AppText.savedRecipesSub(language), Icons.AutoMirrored.Filled.Assignment, SoilGrey, onSaved)
    )

    BaseHomeScreen(
        title = AppText.consumerHomeTitle(language),
        subtitle = AppText.consumerHomeSubtitle(language, state.activeProfile?.fullName.orEmpty()),
        chip = "Profile ready - ${state.activeProfile?.stockStatus ?: "READY_TO_BUY"}",
        features = features,
        onSettings = onSettings,
        primaryAction = {
            Button(onClick = onBuy, modifier = Modifier.fillMaxWidth()) {
                Text(if (activeRequest == null) AppText.raiseRequest(language) else AppText.viewActiveRequest(language))
            }
        },
        secondaryAction = {
            OutlinedButton(onClick = onProfileSetup, modifier = Modifier.fillMaxWidth()) {
                Text(AppText.editConsumerDetails(language))
            }
        },
        quickActionsLabel = AppText.quickActions(language)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseHomeScreen(
    title: String,
    subtitle: String,
    chip: String?,
    features: List<HomeFeature>,
    onSettings: () -> Unit,
    primaryAction: @Composable () -> Unit,
    secondaryAction: @Composable (() -> Unit)? = null,
    quickActionsLabel: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(subtitle, style = MaterialTheme.typography.bodyLarge)
                    chip?.let {
                        AssistChip(onClick = {}, label = { Text(it) })
                    }
                }
            }
            item(span = { GridItemSpan(2) }) { primaryAction() }
            secondaryAction?.let { action ->
                item(span = { GridItemSpan(2) }) { action() }
            }
            item(span = { GridItemSpan(2) }) {
                Text(quickActionsLabel, style = MaterialTheme.typography.titleLarge)
            }
            items(features) { feature ->
                FeatureCard(
                    icon = feature.icon,
                    title = feature.title,
                    subtitle = feature.subtitle,
                    tint = feature.tint,
                    onClick = feature.onClick
                )
            }
        }
    }
}

@Composable
private fun ActiveRequestDialog(
    language: AppLanguage,
    request: ConsumerMilletRequestEntity,
    onDismiss: () -> Unit,
    onViewRequest: () -> Unit,
    onMarkFulfilled: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppText.activeRequest(language)) },
        text = {
            Text(
                "Farmers can still view your request for ${request.milletType} (${request.quantityKg}kg) in ${request.consumerLocation}. Keep it active until the deal is closed."
            )
        },
        confirmButton = {
            TextButton(onClick = onViewRequest) {
                Text("View request")
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onMarkFulfilled) {
                    Text(AppText.markFulfilled(language))
                }
                TextButton(onClick = onDelete) {
                    Text(AppText.deleteRequest(language))
                }
            }
        }
    )
}

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
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.ui.components.FeatureCard
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
    onSettings: () -> Unit,
    onMandi: () -> Unit,
    onRecipes: () -> Unit,
    onHealth: () -> Unit,
    onSaved: () -> Unit,
    onProfileSetup: () -> Unit
) {
    val features = listOf(
        HomeFeature("Mandi Watch", "Browse market rates before signing in", Icons.AutoMirrored.Filled.ShowChart, ForestGreen, onMandi),
        HomeFeature("Recipe Lab", "Explore millet meals and quick cooking ideas", Icons.Default.LocalFlorist, HarvestAmber, onRecipes),
        HomeFeature("Health Benefits", "Understand millet nutrition in a friendlier way", Icons.Default.MedicalServices, TerracottaRed, onHealth),
        HomeFeature("Saved Recipes", "Open saved ideas whenever you want", Icons.AutoMirrored.Filled.Assignment, SoilGrey, onSaved)
    )
    BaseHomeScreen(
        title = "Siri-Dhanya Hub",
        subtitle = "Browse first, then login only when you want to become a farmer seller or a real millet buyer.",
        chip = null,
        features = features,
        onSettings = onSettings,
        primaryAction = {
            Button(onClick = onProfileSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Login / Signup and complete profile")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
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
        HomeFeature("Mandi Watch", "Check mandi prices and publish or refresh your stock", Icons.AutoMirrored.Filled.ShowChart, ForestGreen, onMandi),
        HomeFeature("Farmer Requests", "See nearby consumer requests and contact them outside the app", Icons.Default.NotificationsActive, TerracottaRed, onFarmerRequests),
        HomeFeature("Recipe Lab", "Use recipe ideas to understand use-cases for your millets", Icons.Default.LocalFlorist, HarvestAmber, onRecipes),
        HomeFeature("Health Benefits", "Know the nutrition story behind the millet you grow", Icons.Default.MedicalServices, SoilGrey, onHealth)
    )
    BaseHomeScreen(
        title = "Farmer Home",
        subtitle = "Welcome back, ${state.activeProfile?.fullName.orEmpty()}. Publish stock, watch mandi trends, and track buyer interest from one place.",
        chip = "Profile ready - ${state.activeProfile?.stockStatus ?: "READY_FOR_STOCK"}",
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
                        Text("Update stock status", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            } else {
                Button(onClick = onMandi, modifier = Modifier.fillMaxWidth()) {
                    Text("Publish your first stock")
                }
            }
        },
        secondaryAction = {
            OutlinedButton(onClick = onProfileSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Edit farmer details")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerHomeScreen(
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
        HomeFeature("Buy from Real Farmers", "Raise a millet request and let nearby farmers notice it", Icons.Default.ShoppingCart, ForestGreen, onBuy),
        HomeFeature("Recipe Lab", "Try millet dishes and save your favourite ones", Icons.Default.LocalFlorist, HarvestAmber, onRecipes),
        HomeFeature("Health Benefits", "Explore wellness and nutrition benefits millet by millet", Icons.Default.MedicalServices, TerracottaRed, onHealth),
        HomeFeature("Saved Recipes", "Keep your favourite recipes ready for later", Icons.AutoMirrored.Filled.Assignment, SoilGrey, onSaved)
    )

    BaseHomeScreen(
        title = "Consumer Home",
        subtitle = "Welcome back, ${state.activeProfile?.fullName.orEmpty()}. Find recipes, learn health value, and request millets from real nearby farmers.",
        chip = "Profile ready - ${state.activeProfile?.stockStatus ?: "READY_TO_BUY"}",
        features = features,
        onSettings = onSettings,
        primaryAction = {
            Button(onClick = onBuy, modifier = Modifier.fillMaxWidth()) {
                Text(if (activeRequest == null) "Raise a millet request" else "View active millet request")
            }
        },
        secondaryAction = {
            OutlinedButton(onClick = onProfileSetup, modifier = Modifier.fillMaxWidth()) {
                Text("Edit consumer details")
            }
        }
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
    secondaryAction: @Composable (() -> Unit)? = null
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
                Text("Quick actions", style = MaterialTheme.typography.titleLarge)
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
    request: ConsumerMilletRequestEntity,
    onDismiss: () -> Unit,
    onViewRequest: () -> Unit,
    onMarkFulfilled: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your millet request is still active") },
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
                    Text("Mark fulfilled")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete request")
                }
            }
        }
    )
}

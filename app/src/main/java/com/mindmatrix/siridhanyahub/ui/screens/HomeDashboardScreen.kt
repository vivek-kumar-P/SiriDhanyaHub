package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.ui.components.FeatureCard
import com.mindmatrix.siridhanyahub.ui.theme.ForestGreen
import com.mindmatrix.siridhanyahub.ui.theme.HarvestAmber
import com.mindmatrix.siridhanyahub.ui.theme.SoilGrey
import com.mindmatrix.siridhanyahub.ui.theme.TerracottaRed
import com.mindmatrix.siridhanyahub.viewmodel.HomeViewModel

data class HomeFeature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    onSettings: () -> Unit,
    onMandi: () -> Unit,
    onRecipes: () -> Unit,
    onHealth: () -> Unit,
    onBuy: () -> Unit,
    onSaved: () -> Unit,
    onRoleSetup: (UserRole) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val features = listOf(
        HomeFeature("Mandi Watch", "Track live millet price signals", Icons.AutoMirrored.Filled.ShowChart, ForestGreen, onMandi),
        HomeFeature("Recipe Lab", "Browse practical millet recipes", Icons.Default.LocalFlorist, HarvestAmber, onRecipes),
        HomeFeature("Health Benefits", "Quick nutrition and wellness facts", Icons.Default.MedicalServices, TerracottaRed, onHealth),
        HomeFeature("Direct Buy", "Login required for purchase requests", Icons.Default.ShoppingCart, SoilGrey, onBuy),
        HomeFeature("Saved Recipes", "Your favourites in one place", Icons.Default.Favorite, ForestGreen, onSaved)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Siri-Dhanya Hub") },
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
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = if (state.isLoggedIn) "Welcome back, ${state.userName}" else "Browse first, login only when you need account features.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item(span = { GridItemSpan(2) }) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.activeRole?.let { role ->
                        AssistChip(
                            onClick = {},
                            label = { Text(if (role == UserRole.FARMER) "Farmer profile active" else "Consumer profile active") }
                        )
                    }
                }
            }
            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Choose how you want to use the app", style = MaterialTheme.typography.titleLarge)
                    Button(
                        onClick = { onRoleSetup(UserRole.FARMER) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("I am a farmer who wants to sell millets")
                    }
                    Button(
                        onClick = { onRoleSetup(UserRole.CONSUMER) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("I am a consumer who wants to buy millets")
                    }
                    Text(
                        text = "Your role personalizes labels, profile details, and analytics, but you can still browse freely.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            item(span = { GridItemSpan(2) }) {
                Text("Explore", style = MaterialTheme.typography.titleLarge)
            }
            items(features) { feature ->
                FeatureCard(
                    icon = feature.icon,
                    title = feature.title,
                    subtitle = feature.subtitle,
                    tint = feature.tint,
                    visible = true,
                    onClick = feature.onClick
                )
            }
            item(span = { GridItemSpan(2) }) {
                Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Quick pulse", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "A clean, utility-first home keeps the app fast to scan, like a payments dashboard but tuned for millet decisions.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    AssistChip(
                        onClick = onMandi,
                        label = { Text("Open market prices") },
                        leadingIcon = { Icon(Icons.Default.Bolt, contentDescription = null) }
                    )
                }
            }
        }
    }
}

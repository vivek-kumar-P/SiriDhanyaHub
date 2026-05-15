package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.data.repository.FirestoreFarmerListing
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.AppText
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.DirectBuyViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

private val milletOptions = listOf(
    "Ragi", "Jowar", "Bajra", "Foxtail Millet", "Little Millet", "Barnyard Millet"
)
private val sourceLocations = listOf(
    "Bengaluru", "Davangere", "Mysuru", "Hubli", "Mandya", "Tumakuru", "Shivamogga"
)
private val purposes = listOf(
    "Marriage", "Family function", "Community gathering",
    "Restaurant", "Retail", "Personal use"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectBuyScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    viewModel: DirectBuyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showFulfillmentDialog by remember { mutableStateOf(false) }
    val activeRequest = state.activeRequest
    val quantity = state.draft.quantityKg.toIntOrNull() ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppText.directBuyTitle(language)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showFulfillmentDialog && activeRequest != null) {
            FulfillmentDialog(
                milletType = activeRequest.milletType,
                requestedQuantityKg = activeRequest.quantityKg,
                onDismiss = { showFulfillmentDialog = false },
                onCompleted = {
                    showFulfillmentDialog = false
                    viewModel.deleteRequest() // clear active request after fulfillment
                }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (state.activeRole != UserRole.CONSUMER) {
                item {
                    Text(
                        AppText.consumerOnlyPage(language),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                return@LazyColumn
            }

            // ── SECTION 1: Available Farmers ──
            item {
                Text(
                    AppText.availableFarmers(language),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Text(
                    if (state.draft.milletType.isBlank())
                        AppText.noFarmersYet(language)
                    else
                        AppText.availableFarmers(language),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (state.availableFarmers.isEmpty()) {
                item {
                    Text(
                        AppText.noFarmersYet(language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.availableFarmers, key = { it.farmerUserId }) { farmer ->
                    FarmerListingCard(farmer = farmer, language = language)
                }
            }

            // ── DIVIDER ──
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // ── SECTION 2: Raise a Request ──
            item {
                Text(
                    AppText.raiseMilletRequest(language),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Text(
                    AppText.directBuyHint(language),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Active request card
            if (activeRequest != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                AppText.activeRequest(language),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "${activeRequest.milletType} — ${activeRequest.quantityKg}kg",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Location: ${activeRequest.consumerLocation}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Source preference: ${activeRequest.preferredSourceLocation}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { showFulfillmentDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(AppText.markFulfilled(language))
                                }
                                OutlinedButton(
                                    onClick = viewModel::deleteRequest,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(AppText.deleteRequest(language))
                                }
                            }
                        }
                    }
                }
            }

            // Request form
            item {
                SelectionField(
                    title = "Type of millet needed",
                    selected = state.draft.milletType,
                    options = milletOptions,
                    onSelect = viewModel::updateMilletType
                )
            }
            item {
                OutlinedTextField(
                    value = state.draft.quantityKg,
                    onValueChange = viewModel::updateQuantity,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Quantity needed (kg)") }
                )
            }
            item {
                OutlinedTextField(
                    value = state.draft.neededDate,
                    onValueChange = viewModel::updateNeededDate,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Date needed (YYYY-MM-DD)") }
                )
            }
            item {
                OutlinedTextField(
                    value = state.draft.neededTime,
                    onValueChange = viewModel::updateNeededTime,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Time needed (HH:MM)") }
                )
            }
            item {
                OutlinedTextField(
                    value = state.draft.consumerLocation,
                    onValueChange = viewModel::updateLocation,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your location / city") }
                )
            }
            item {
                SelectionField(
                    title = "From where do you want to buy millets?",
                    selected = state.draft.preferredSourceLocation,
                    options = sourceLocations,
                    onSelect = viewModel::updatePreferredSourceLocation
                )
            }
            if (quantity > 100) {
                item {
                    SelectionField(
                        title = "Purpose of requirement",
                        selected = state.draft.purpose,
                        options = purposes,
                        onSelect = viewModel::updatePurpose
                    )
                }
            }
            item {
                state.message?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (it.contains("active", ignoreCase = true))
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
                Button(
                    onClick = { viewModel.saveRequest {} },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                ) {
                    Text(
                        if (state.isSaving) AppText.saving(language)
                        else AppText.saveRequest(language)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FarmerListingCard(farmer: FirestoreFarmerListing, language: AppLanguage) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WheatSurface),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(farmer.farmerName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${farmer.milletType} • ${farmer.quantityAvailableKg}kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        farmer.marketCity,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        "₹${farmer.selectedMarketRate.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "per quintal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                DetailRow("📞 Mobile", farmer.farmerMobile)
                DetailRow("📍 District", farmer.farmerDistrict)
                DetailRow("🏪 Market", "${farmer.marketPlace}, ${farmer.marketCity}")
                DetailRow("🌾 Millet", farmer.milletType)
                DetailRow("⚖️ Available", "${farmer.quantityAvailableKg} kg")
                DetailRow("📅 Grown over", "${farmer.growthDurationDays} days")
                DetailRow("🌱 Grown area", farmer.grownArea)
                if (farmer.stockNote.isNotBlank()) {
                    DetailRow("📝 Note", farmer.stockNote)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    "Tap the mobile number above to call this farmer directly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "Tap to see full details",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.55f)
        )
    }
}

@Composable
private fun SelectionField(
    title: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        options.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { option ->
                    OutlinedButton(
                        onClick = { onSelect(option) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (selected == option) "✓ $option" else option)
                    }
                }
            }
        }
    }
}
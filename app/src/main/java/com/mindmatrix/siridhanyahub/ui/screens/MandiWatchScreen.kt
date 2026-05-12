package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import com.mindmatrix.siridhanyahub.data.repository.MandiOption
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.MandiViewModel

private val supportedCities = listOf("Bengaluru", "Davangere", "Mysuru", "Hubli")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiWatchScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    viewModel: MandiViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedOption = state.selectedOption
    val farmerProfile = state.farmerProfile
    var showReview by remember { mutableStateOf(false) }

    if (showReview && selectedOption != null && farmerProfile != null) {
        AlertDialog(
            onDismissRequest = { showReview = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReview = false
                        viewModel.publishStock {}
                    }
                ) {
                    Text("Confirm stock availability")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReview = false }) {
                    Text("Reselect")
                }
            },
            title = { Text("Confirm farmer stock") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Farmer: ${farmerProfile.fullName}")
                    Text("Contact: ${farmerProfile.contactNumber}")
                    Text("Selected mandi: ${selectedOption.marketPlace}, ${selectedOption.marketCity}")
                    Text("Chosen rate: Rs.${selectedOption.selectedRate.toInt()} per quintal")
                    Text("Millet: ${state.stockDraft.milletType}")
                    Text("Quantity: ${state.stockDraft.quantityAvailableKg}kg")
                    Text("Growth time: ${state.stockDraft.growthDurationDays} days")
                    Text("Grown area: ${state.stockDraft.grownArea}")
                    if (state.stockDraft.stockNote.isNotBlank()) {
                        Text("Note: ${state.stockDraft.stockNote}")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Mandi Watch") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (!state.isFarmer) {
                    Text("This page is only for farmer accounts.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text(
                        "Select a city, inspect the current mandi cards, choose the exact place and rate you prefer, then publish your live stock details.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            if (state.isFarmer) {
                item {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search city or millet") }
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        supportedCities.forEach { city ->
                            OutlinedButton(
                                onClick = { viewModel.selectCity(city) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (city == state.selectedCity) "$city *" else city)
                            }
                        }
                    }
                }
                item {
                    Text("Prices across Karnataka markets", style = MaterialTheme.typography.titleLarge)
                }
                items(state.prices, key = { it.id }) { price ->
                    PriceCard(price = price, onClick = { viewModel.openPrice(price) })
                }
                if (state.selectedPrice != null) {
                    item {
                        Text("Choose one exact place and rate", style = MaterialTheme.typography.titleLarge)
                    }
                    items(state.mandiOptions) { option ->
                        MandiOptionCard(
                            option = option,
                            selected = state.selectedOption == option,
                            onClick = { viewModel.selectMandiOption(option) }
                        )
                    }
                }
                if (selectedOption != null) {
                    item {
                        Text("Available millet stock", style = MaterialTheme.typography.titleLarge)
                    }
                    item {
                        OutlinedTextField(
                            value = state.stockDraft.milletType,
                            onValueChange = viewModel::updateMilletType,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Millet type") }
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = state.stockDraft.quantityAvailableKg,
                            onValueChange = viewModel::updateQuantity,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Available quantity (kg)") }
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = state.stockDraft.growthDurationDays,
                            onValueChange = viewModel::updateGrowthDuration,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Time taken to grow (days)") }
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = state.stockDraft.grownArea,
                            onValueChange = viewModel::updateGrownArea,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Millets grown in which area") }
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = state.stockDraft.stockNote,
                            onValueChange = viewModel::updateStockNote,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Stock note (optional)") }
                        )
                    }
                    item {
                        state.message?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(
                            onClick = { showReview = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isPublishing
                        ) {
                            Text(if (state.isPublishing) "Publishing..." else "Review and confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceCard(
    price: PriceEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = WheatSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(price.milletType, style = MaterialTheme.typography.titleLarge)
                Text(price.city, style = MaterialTheme.typography.bodyMedium)
                Text("Trend: ${price.trendDirection}", style = MaterialTheme.typography.bodySmall)
            }
            Text("Rs.${price.pricePerQuintal.toInt()}", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun MandiOptionCard(
    option: MandiOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = if (selected) WheatSurface else MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(option.marketPlace, style = MaterialTheme.typography.titleMedium)
            Text("${option.marketCity} - ${option.milletType}", style = MaterialTheme.typography.bodyMedium)
            Text("Rate: Rs.${option.selectedRate.toInt()} per quintal", style = MaterialTheme.typography.bodyMedium)
            Text(if (selected) "Selected" else "Tap to select", style = MaterialTheme.typography.bodySmall)
        }
    }
}

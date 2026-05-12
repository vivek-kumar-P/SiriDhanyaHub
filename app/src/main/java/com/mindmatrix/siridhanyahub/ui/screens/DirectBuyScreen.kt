package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.DirectBuyViewModel

private val milletOptions = listOf("Ragi", "Jowar", "Bajra", "Foxtail Millet", "Little Millet", "Barnyard Millet")
private val sourceLocations = listOf("Bengaluru", "Davangere", "Mysuru", "Hubli", "Mandya", "Tumakuru", "Shivamogga")
private val purposes = listOf("Marriage", "Family function", "Community gathering", "Restaurant", "Retail", "Personal use")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectBuyScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    viewModel: DirectBuyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activeRequest = state.activeRequest
    val quantity = state.draft.quantityKg.toIntOrNull() ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buy millets from real farmers") },
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
                Text(
                    "Raise one active request. Nearby farmers with live stock in the same or nearby mandi areas will see it in their in-app request page.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (state.activeRole != UserRole.CONSUMER) {
                item {
                    Text("This page is only for consumer accounts.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                item {
                    if (activeRequest != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Active request", style = MaterialTheme.typography.titleLarge)
                                Text("${activeRequest.milletType} - ${activeRequest.quantityKg}kg", style = MaterialTheme.typography.bodyLarge)
                                Text("Location: ${activeRequest.consumerLocation}", style = MaterialTheme.typography.bodyMedium)
                                Text("Source preference: ${activeRequest.preferredSourceLocation}", style = MaterialTheme.typography.bodyMedium)
                                OutlinedButton(onClick = viewModel::markFulfilled) {
                                    Text("Mark fulfilled")
                                }
                                OutlinedButton(onClick = viewModel::deleteRequest) {
                                    Text("Delete request")
                                }
                            }
                        }
                    }
                }
                item {
                    SelectionField("Type of millet needed", state.draft.milletType, milletOptions, viewModel::updateMilletType)
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
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                    Button(
                        onClick = { viewModel.saveRequest {} },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving
                    ) {
                        Text(if (state.isSaving) "Saving..." else "Save request")
                    }
                }
            }
        }
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
                        Text(if (selected == option) "$option *" else option)
                    }
                }
            }
        }
    }
}

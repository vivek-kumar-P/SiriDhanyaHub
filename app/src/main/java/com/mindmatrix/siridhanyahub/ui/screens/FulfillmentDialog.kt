package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.repository.FirestoreFarmerListing
import com.mindmatrix.siridhanyahub.ui.theme.HarvestAmber
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.FulfillmentStep
import com.mindmatrix.siridhanyahub.viewmodel.FulfillmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FulfillmentDialog(
    milletType: String,
    requestedQuantityKg: Int,
    onDismiss: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: FulfillmentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(milletType) {
        viewModel.loadFarmers(milletType, requestedQuantityKg)
    }

    when (state.step) {

        // ── Step 1: Pick which farmer you bought from ──
        FulfillmentStep.PICK_FARMER -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Who did you buy from?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Select the farmer you completed the deal with.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (state.availableFarmers.isEmpty()) {
                            Text(
                                "No matching farmers found. Make sure the farmer has published stock.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.availableFarmers) { farmer ->
                                FarmerPickCard(
                                    farmer = farmer,
                                    onSelect = { viewModel.selectFarmer(farmer) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            )
        }

        // ── Step 2: Confirm actual quantity purchased ──
        FulfillmentStep.CONFIRM_QUANTITY -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Confirm quantity purchased") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.selectedFarmer?.let { farmer ->
                            Text(
                                "Farmer: ${farmer.farmerName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Rate: ₹${farmer.selectedMarketRate.toInt()} per quintal",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            val qty = state.quantityKg.toIntOrNull() ?: 0
                            val total = (qty / 100.0) * farmer.selectedMarketRate
                            Text(
                                "Estimated total: ₹${total.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        OutlinedTextField(
                            value = state.quantityKg,
                            onValueChange = viewModel::updateQuantity,
                            label = { Text("Actual quantity purchased (kg)") },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                Text("Your original request was ${requestedQuantityKg}kg. Adjust if actual purchase differed.")
                            }
                        )
                        state.message?.let {
                            Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmQuantity() },
                        enabled = (state.quantityKg.toIntOrNull() ?: 0) > 0
                    ) {
                        Text("Next — Rate this farmer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            )
        }

        // ── Step 3: Rate the farmer ──
        FulfillmentStep.RATE_FARMER -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Rate this farmer") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "How was your experience with ${state.selectedFarmer?.farmerName}?",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Star rating
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..5).forEach { star ->
                                IconButton(onClick = { viewModel.setRating(star) }) {
                                    Icon(
                                        imageVector = if (star <= state.rating)
                                            Icons.Filled.Star
                                        else
                                            Icons.Outlined.StarOutline,
                                        contentDescription = "$star stars",
                                        tint = if (star <= state.rating)
                                            HarvestAmber
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = state.comment,
                            onValueChange = viewModel::updateComment,
                            label = { Text("Comment (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        state.message?.let {
                            Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.submitFulfillment {
                                onCompleted()
                            }
                        },
                        enabled = !state.isSaving && state.rating > 0
                    ) {
                        Text(if (state.isSaving) "Submitting..." else "Submit and complete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            )
        }

        FulfillmentStep.DONE -> {
            // Handled by onCompleted callback
        }
    }
}

@Composable
private fun FarmerPickCard(
    farmer: FirestoreFarmerListing,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WheatSurface),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(farmer.farmerName, style = MaterialTheme.typography.titleSmall)
                Text(
                    "${farmer.milletType} • ${farmer.quantityAvailableKg}kg • ₹${farmer.selectedMarketRate.toInt()}/quintal",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    farmer.marketPlace,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(onClick = onSelect) {
                Text("Select")
            }
        }
    }
}
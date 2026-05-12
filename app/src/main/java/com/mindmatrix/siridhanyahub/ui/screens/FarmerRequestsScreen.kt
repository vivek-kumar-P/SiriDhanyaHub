package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.FarmerRequestsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerRequestsScreen(
    onBack: () -> Unit,
    viewModel: FarmerRequestsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Requests") },
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
            if (!state.isFarmer) {
                item {
                    Text("Only farmer accounts can open this page.", style = MaterialTheme.typography.bodyLarge)
                }
                return@LazyColumn
            }
            item {
                Text(
                    "Consumers will contact you outside the app using the mobile number shown here. This page is your in-app notification board for active millet requests.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (state.items.isEmpty()) {
                item {
                    Text("No matching consumer requests yet. Publish stock first and check back soon.", style = MaterialTheme.typography.bodyMedium)
                }
            }
            items(state.items, key = { it.matchId }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WheatSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(item.consumerName, style = MaterialTheme.typography.titleLarge)
                        Text("Mobile: ${item.consumerMobile}", style = MaterialTheme.typography.bodyLarge)
                        Text("Location: ${item.consumerDistrict} • ${item.consumerAddress}", style = MaterialTheme.typography.bodyMedium)
                        Text("Millet: ${item.milletType} • ${item.quantityKg}kg", style = MaterialTheme.typography.bodyMedium)
                        Text("Needed at: ${formatDate(item.neededAtMillis)}", style = MaterialTheme.typography.bodyMedium)
                        Text("Preferred source: ${item.preferredSourceLocation}", style = MaterialTheme.typography.bodyMedium)
                        Text("Matched from: ${item.matchLocationContext}", style = MaterialTheme.typography.bodySmall)
                        item.purpose?.takeIf { it.isNotBlank() }?.let { purpose ->
                            Text("Purpose: $purpose", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

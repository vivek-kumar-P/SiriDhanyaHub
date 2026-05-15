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
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.AppText
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.FarmerRequestsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerRequestsScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    viewModel: FarmerRequestsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppText.consumerRequestsTitle(language)) },
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
                    Text(
                        AppText.farmerOnlyPage(language),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                return@LazyColumn
            }

            item {
                Text(
                    AppText.consumerRequestsHint(language),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (state.items.isEmpty()) {
                item {
                    Text(
                        AppText.noConsumerRequestsYet(language),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            items(state.items, key = { it.consumerUserId }) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WheatSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(request.consumerName, style = MaterialTheme.typography.titleLarge)
                        Text("Mobile: ${request.consumerMobile}", style = MaterialTheme.typography.bodyLarge)
                        Text("District: ${request.consumerDistrict}", style = MaterialTheme.typography.bodyMedium)
                        Text("Location: ${request.consumerLocation}", style = MaterialTheme.typography.bodyMedium)
                        Text("Millet: ${request.milletType} • ${request.quantityKg}kg", style = MaterialTheme.typography.bodyMedium)
                        Text("Needed by: ${formatDate(request.neededAtMillis)}", style = MaterialTheme.typography.bodyMedium)
                        Text("Preferred source: ${request.preferredSourceLocation}", style = MaterialTheme.typography.bodyMedium)
                        request.purpose.takeIf { it.isNotBlank() }?.let {
                            Text("Purpose: $it", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
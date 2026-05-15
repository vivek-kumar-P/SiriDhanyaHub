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
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.local.entity.FeedbackEntity
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.ui.theme.HarvestAmber
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.AnalyticsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.records.isEmpty() && state.feedback.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "No activity yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Complete a transaction to see your analytics here.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    if (state.role == UserRole.FARMER) "Farmer Dashboard" else "Consumer Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // ── Summary cards ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryTile(
                        label = "Total kg",
                        value = "${state.totalKg} kg",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryTile(
                        label = if (state.role == UserRole.FARMER) "Total earned" else "Total spent",
                        value = "₹${state.totalAmount.toInt()}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryTile(
                        label = "Transactions",
                        value = "${state.records.size}",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryTile(
                        label = if (state.role == UserRole.FARMER) "Unique buyers" else "Farmers bought from",
                        value = "${state.uniqueCounterparties}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Rating (farmer only) ──
            if (state.role == UserRole.FARMER && state.feedback.isNotEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = HarvestAmber
                            )
                            Column {
                                Text(
                                    "Average rating",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "${"%.1f".format(state.averageRating)} / 5.0 from ${state.feedback.size} reviews",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Customer feedback",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(state.feedback) { feedback ->
                    FeedbackRow(feedback)
                }
            }

            // ── Transaction history ──
            if (state.records.isNotEmpty()) {
                item {
                    Text(
                        "Transaction history",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(state.records.take(10)) { record ->
                    TransactionRow(record)
                }
            }
        }
    }
}

@Composable
private fun SummaryTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = WheatSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FeedbackRow(feedback: FeedbackEntity) {
    Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(feedback.rating) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = HarvestAmber,
                        modifier = Modifier.padding(0.dp)
                    )
                }
                Text(
                    feedback.consumerName,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Text(
                "${feedback.milletType} • ${feedback.quantityKg}kg",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (feedback.comment.isNotBlank()) {
                Text(
                    feedback.comment,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(record: TransactionRecordEntity) {
    Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "${record.transactionType.lowercase().replaceFirstChar(Char::titlecase)} • ${record.milletType} • ${record.quantityKg}kg",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                "${record.counterpartyName} • ${record.referenceContext}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            record.amountEstimate?.let {
                Text(
                    "₹${it.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                formatAnalyticsDate(record.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatAnalyticsDate(time: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(time))
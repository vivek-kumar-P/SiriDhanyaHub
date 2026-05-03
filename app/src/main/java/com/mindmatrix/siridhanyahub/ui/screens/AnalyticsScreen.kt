package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.local.entity.TransactionRecordEntity
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.ui.components.AnalyticsSummaryCard
import com.mindmatrix.siridhanyahub.ui.components.MiniBarChart
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
    var selectedMillet by remember { mutableStateOf<String?>(null) }

    val totalCount = state.records.size
    val totalQuantity = state.records.sumOf { it.quantityKg }
    val buyCount = state.records.count { it.transactionType == "BUY" }
    val sellCount = state.records.count { it.transactionType == "SELL" }
    val groupedByMillet = state.records
        .groupBy { it.milletType }
        .mapValues { (_, items) -> items.sumOf { it.quantityKg }.toFloat() }
        .entries
        .sortedByDescending { it.value }
        .map { it.key to it.value }
    val groupedByType = listOf(
        "Buy" to buyCount.toFloat(),
        "Sell" to sellCount.toFloat()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.records.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (state.role == UserRole.FARMER) "No sell-side activity yet" else "No transaction activity yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "As soon as you request a buy or log a sell intent, this area will start showing totals and graphs.",
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
                    text = if (state.role == UserRole.FARMER) "Farmer analytics overview" else "Consumer analytics overview",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        AnalyticsSummaryCard("Transactions", totalCount.toString(), "All recorded buy/sell actions")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        AnalyticsSummaryCard("Quantity", "$totalQuantity kg", "Combined volume across actions")
                    }
                }
            }
            item {
                MiniBarChart(
                    title = "Volume by millet",
                    items = groupedByMillet,
                    selectedLabel = selectedMillet,
                    onSelect = { selectedMillet = it }
                )
            }
            item {
                MiniBarChart(
                    title = "Buy vs sell count",
                    items = groupedByType,
                    selectedLabel = null,
                    onSelect = {}
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WheatSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Past transactions", style = MaterialTheme.typography.titleMedium)
                        state.records.take(8).forEach { record ->
                            TransactionRow(record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(record: TransactionRecordEntity) {
    Column {
        Text(
            text = "${record.transactionType.lowercase().replaceFirstChar(Char::titlecase)} • ${record.milletType} • ${record.quantityKg} kg",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "${record.counterpartyName} • ${record.referenceContext} • ${formatAnalyticsDate(record.createdAt)}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatAnalyticsDate(time: Long): String {
    return SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(time))
}

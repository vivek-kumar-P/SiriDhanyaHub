package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.local.entity.PriceEntity
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.MandiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val supportedCities = listOf("Bengaluru", "Davangere", "Mysuru", "Hubli")

@Composable
fun MandiWatchScreen(
    language: AppLanguage,
    viewModel: MandiViewModel = hiltViewModel()
) {
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val prices by viewModel.prices.collectAsStateWithLifecycle()
    val selectedPrice by viewModel.selectedPrice.collectAsStateWithLifecycle()
    val trend by viewModel.trend.collectAsStateWithLifecycle()
    val canLogSellIntent by viewModel.canLogSellIntent.collectAsStateWithLifecycle()

    if (selectedPrice != null) {
        PriceTrendScreen(
            language = language,
            selectedPrice = selectedPrice!!,
            trend = trend,
            canLogSellIntent = canLogSellIntent,
            onLogSellIntent = viewModel::recordSellIntent,
            onBack = viewModel::closeTrend
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Mandi Watch", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = mandiSubtitle(language),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(supportedCities) { city ->
                if (city == selectedCity) {
                    Button(onClick = { viewModel.selectCity(city) }) {
                        Text(city)
                    }
                } else {
                    OutlinedButton(onClick = { viewModel.selectCity(city) }) {
                        Text(city)
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(prices, key = { it.id }) { price ->
                PriceCard(price = price, onClick = { viewModel.showTrend(price) })
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
                Text(
                    text = "Updated ${formatDate(price.lastUpdated)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column {
                Text("Rs.${price.pricePerQuintal.toInt()}", style = MaterialTheme.typography.titleLarge)
                Text(trendLabel(price.trendDirection), style = MaterialTheme.typography.bodyMedium)
                Text("Tap for trend", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PriceTrendScreen(
    language: AppLanguage,
    selectedPrice: PriceEntity,
    trend: List<PriceEntity>,
    canLogSellIntent: Boolean,
    onLogSellIntent: () -> Unit,
    onBack: () -> Unit
) {
    val high = trend.maxOfOrNull { it.pricePerQuintal } ?: selectedPrice.pricePerQuintal
    val low = trend.minOfOrNull { it.pricePerQuintal } ?: selectedPrice.pricePerQuintal

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedButton(onClick = onBack) {
            Text(backToPricesLabel(language))
        }
        Text(
            text = "${selectedPrice.milletType} in ${selectedPrice.city}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TrendMetricCard(todayLabel(language), "Rs.${selectedPrice.pricePerQuintal.toInt()}")
            TrendMetricCard(highLabel(language), "Rs.${high.toInt()}")
            TrendMetricCard(lowLabel(language), "Rs.${low.toInt()}")
        }
        if (canLogSellIntent) {
            Button(
                onClick = onLogSellIntent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Record sell intent")
            }
        }
        LazyColumn(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trend, key = { it.id }) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatDate(item.dateRecorded))
                        Text("Rs.${item.pricePerQuintal.toInt()}  ${trendLabel(item.trendDirection)}")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TrendMetricCard(label: String, value: String) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = WheatSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun trendLabel(direction: String): String = when (direction) {
    "UP" -> "Up"
    "DOWN" -> "Down"
    else -> "Stable"
}

private fun mandiSubtitle(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Compare millet prices across Karnataka markets."
    AppLanguage.KANNADA -> "ಕರ್ನಾಟಕ ಮಾರುಕಟ್ಟೆಗಳಲ್ಲಿ ಸಿರಿಧಾನ್ಯ ಬೆಲೆಗಳನ್ನು ಹೋಲಿಸಿ."
    AppLanguage.HINDI -> "कर्नाटक की मंडियों में मिलेट कीमतों की तुलना करें।"
}

private fun backToPricesLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Back to prices"
    AppLanguage.KANNADA -> "ಬೆಲೆಗಳಿಗೆ ಹಿಂತಿರುಗಿ"
    AppLanguage.HINDI -> "कीमतों पर वापस जाएँ"
}

private fun todayLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Today"
    AppLanguage.KANNADA -> "ಇಂದು"
    AppLanguage.HINDI -> "आज"
}

private fun highLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "High"
    AppLanguage.KANNADA -> "ಹೆಚ್ಚು"
    AppLanguage.HINDI -> "उच्च"
}

private fun lowLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Low"
    AppLanguage.KANNADA -> "ಕಡಿಮೆ"
    AppLanguage.HINDI -> "न्यून"
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
}

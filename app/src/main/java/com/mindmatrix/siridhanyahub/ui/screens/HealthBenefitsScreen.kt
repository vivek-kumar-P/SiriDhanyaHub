package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.mindmatrix.siridhanyahub.data.local.entity.HealthBenefitEntity
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.theme.TerracottaRed
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthBenefitsScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val selectedMilletType by viewModel.selectedMilletType.collectAsStateWithLifecycle()
    val allBenefits by viewModel.allBenefits.collectAsStateWithLifecycle()
    val selectedBenefits by viewModel.selectedBenefits.collectAsStateWithLifecycle()

    if (selectedMilletType != null) {
        HealthDetailScreen(
            language = language,
            milletType = selectedMilletType!!,
            benefits = selectedBenefits,
            onBack = viewModel::closeMillet
        )
        return
    }

    val cards = allBenefits.distinctBy { it.milletType }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Benefits") },
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
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Each millet gets its own quick wellness card so users can compare nutrition, health value, and climate note at a glance.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            items(cards, key = { it.milletType }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openMillet(item.milletType) },
                    colors = CardDefaults.cardColors(containerColor = WheatSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(TerracottaRed.copy(alpha = 0.14f), androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = TerracottaRed)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.milletType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(item.tagline, style = MaterialTheme.typography.bodyMedium)
                            Text(healthPrompt(language), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HealthDetailScreen(
    language: AppLanguage,
    milletType: String,
    benefits: List<HealthBenefitEntity>,
    onBack: () -> Unit
) {
    val summary = benefits.firstOrNull()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(milletType) },
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
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            summary?.let {
                item { Text(it.tagline, style = MaterialTheme.typography.titleMedium) }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(healthBenefitsLabel(language), style = MaterialTheme.typography.titleMedium)
                        benefits.forEach { item ->
                            Text(text = "- ${item.benefitStatement}", modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
            summary?.nutritionalSummary?.let { nutritionalSummary ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(nutritionSummaryLabel(language), style = MaterialTheme.typography.titleMedium)
                            Text(nutritionalSummary, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
            summary?.climateNote?.let { climateNote ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = WheatSurface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(climateNoteLabel(language), style = MaterialTheme.typography.titleMedium)
                            Text(climateNote, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun healthPrompt(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Tap to open the full nutrition and wellness summary."
    AppLanguage.KANNADA -> "à²ªà³‚à²°à³à²£ à²ªà³‹à²·à²•à²¾à²‚à²¶ à²®à²¤à³à²¤à³ à²†à²°à³‹à²—à³à²¯ à²¸à²¾à²°à²¾à²‚à²¶à²µà²¨à³à²¨à³ à²¤à³†à²°à³†à²¯à²²à³ à²¤à²Ÿà³à²Ÿà²¿à²°à²¿."
    AppLanguage.HINDI -> "à¤ªà¥‚à¤°à¤¾ à¤ªà¥‹à¤·à¤£ à¤”à¤° à¤µà¥‡à¤²à¤¨à¥‡à¤¸ à¤¸à¤¾à¤°à¤¾à¤‚à¤¶ à¤–à¥‹à¤²à¤¨à¥‡ à¤•à¥‡ à¤²à¤¿à¤ à¤Ÿà¥ˆà¤ª à¤•à¤°à¥‡à¤‚."
}

private fun healthBenefitsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Health Benefits"
    AppLanguage.KANNADA -> "à²†à²°à³‹à²—à³à²¯ à²²à²¾à²­à²—à²³à³"
    AppLanguage.HINDI -> "à¤¸à¥à¤µà¤¾à¤¸à¥à¤¥à¥à¤¯ à¤²à¤¾à¤­"
}

private fun nutritionSummaryLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Nutrition Summary"
    AppLanguage.KANNADA -> "à²ªà³‹à²·à²•à²¾à²‚à²¶ à²¸à²¾à²°à²¾à²‚à²¶"
    AppLanguage.HINDI -> "à¤ªà¥‹à¤·à¤£ à¤¸à¤¾à¤°à¤¾à¤‚à¤¶"
}

private fun climateNoteLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Climate Action Note"
    AppLanguage.KANNADA -> "à²¹à²µà²¾à²®à²¾à²¨ à²•à³à²°à²® à²Ÿà²¿à²ªà³à²ªà²£à²¿"
    AppLanguage.HINDI -> "à¤œà¤²à¤µà¤¾à¤¯à¥ à¤¨à¥‹à¤Ÿ"
}

package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.mindmatrix.siridhanyahub.data.local.entity.HealthBenefitEntity
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.HealthViewModel

@Composable
fun HealthBenefitsScreen(
    language: AppLanguage,
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

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Health Benefits", style = MaterialTheme.typography.headlineSmall)
        }
        items(cards, key = { it.milletType }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openMillet(item.milletType) },
                colors = CardDefaults.cardColors(containerColor = WheatSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item.milletType, style = MaterialTheme.typography.titleLarge)
                    Text(item.tagline, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = healthPrompt(language),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthDetailScreen(
    language: AppLanguage,
    milletType: String,
    benefits: List<HealthBenefitEntity>,
    onBack: () -> Unit
) {
    val summary = benefits.firstOrNull()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedButton(onClick = onBack) {
            Text(backToHealthCardsLabel(language))
        }
        Text(
            text = milletType,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )
        summary?.let {
            Text(
                text = it.tagline,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(containerColor = WheatSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(healthBenefitsLabel(language), style = MaterialTheme.typography.titleMedium)
                benefits.forEach { item ->
                    Text(
                        text = "- ${item.benefitStatement}",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        summary?.nutritionalSummary?.let { nutritionalSummary ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(containerColor = WheatSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(nutritionSummaryLabel(language), style = MaterialTheme.typography.titleMedium)
                    Text(nutritionalSummary, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        summary?.climateNote?.let { climateNote ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(containerColor = WheatSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(climateNoteLabel(language), style = MaterialTheme.typography.titleMedium)
                    Text(climateNote, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

private fun healthPrompt(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Tap to view nutrition, health points, and climate note."
    AppLanguage.KANNADA -> "ಪೋಷಕಾಂಶ, ಆರೋಗ್ಯ ಅಂಶಗಳು ಮತ್ತು ಹವಾಮಾನ ಟಿಪ್ಪಣಿಯನ್ನು ನೋಡಲು ತಟ್ಟಿರಿ."
    AppLanguage.HINDI -> "पोषण, स्वास्थ्य बिंदु और जलवायु नोट देखने के लिए टैप करें।"
}

private fun backToHealthCardsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Back to health cards"
    AppLanguage.KANNADA -> "ಆರೋಗ್ಯ ಕಾರ್ಡ್‌ಗಳಿಗೆ ಹಿಂತಿರುಗಿ"
    AppLanguage.HINDI -> "स्वास्थ्य कार्ड पर वापस जाएँ"
}

private fun healthBenefitsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Health Benefits"
    AppLanguage.KANNADA -> "ಆರೋಗ್ಯ ಲಾಭಗಳು"
    AppLanguage.HINDI -> "स्वास्थ्य लाभ"
}

private fun nutritionSummaryLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Nutrition Summary"
    AppLanguage.KANNADA -> "ಪೋಷಕಾಂಶ ಸಾರಾಂಶ"
    AppLanguage.HINDI -> "पोषण सारांश"
}

private fun climateNoteLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Climate Action Note"
    AppLanguage.KANNADA -> "ಹವಾಮಾನ ಕ್ರಮ ಟಿಪ್ಪಣಿ"
    AppLanguage.HINDI -> "जलवायु नोट"
}

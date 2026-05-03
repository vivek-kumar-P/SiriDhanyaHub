package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.local.entity.FpoEntity
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.theme.WheatSurface
import com.mindmatrix.siridhanyahub.viewmodel.DirectBuyViewModel

@Composable
fun DirectBuyScreen(
    language: AppLanguage,
    viewModel: DirectBuyViewModel = hiltViewModel()
) {
    val fpos by viewModel.fpos.collectAsStateWithLifecycle()
    val selectedFpo by viewModel.selectedFpo.collectAsStateWithLifecycle()
    var feedback by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.message.collect { feedback = it }
    }

    if (selectedFpo != null) {
        DirectBuyDetailScreen(
            language = language,
            fpo = selectedFpo!!,
            feedback = feedback,
            onConsumeFeedback = { feedback = null },
            onRequest = viewModel::requestToBuy,
            onBack = viewModel::closeFpo
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Direct Buy", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Browse FPOs and create buyer-side requests once you are logged in.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
            )
        }
        items(fpos, key = { it.id }) { fpo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openFpo(fpo.id) },
                colors = CardDefaults.cardColors(containerColor = WheatSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(fpo.fpoName, style = MaterialTheme.typography.titleLarge)
                    Text(fpo.district, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Millets: ${fpo.availableMillets}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Text(
                        text = directBuyPrompt(language),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectBuyDetailScreen(
    language: AppLanguage,
    fpo: FpoEntity,
    feedback: String?,
    onConsumeFeedback: () -> Unit,
    onRequest: () -> Unit,
    onBack: () -> Unit
) {
    if (feedback != null) {
        AlertDialog(
            onDismissRequest = onConsumeFeedback,
            confirmButton = {
                TextButton(onClick = onConsumeFeedback) {
                    Text(okLabel(language))
                }
            },
            title = { Text(requestSentLabel(language)) },
            text = { Text(feedback) }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedButton(onClick = onBack) {
            Text(backToFpoLabel(language))
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(containerColor = WheatSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(fpo.fpoName, style = MaterialTheme.typography.headlineSmall)
                Text(fpo.district, style = MaterialTheme.typography.titleMedium)
                Text("Address: ${fpo.address}", modifier = Modifier.padding(top = 10.dp))
                Text("Phone: ${fpo.phone}", modifier = Modifier.padding(top = 6.dp))
                Text("Millets: ${fpo.availableMillets}", modifier = Modifier.padding(top = 6.dp))
                Text("Available: ${fpo.availableQuantityKg} kg", modifier = Modifier.padding(top = 6.dp))
            }
        }
        OutlinedButton(
            onClick = onRequest,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(requestToBuyLabel(language))
        }
    }
}

private fun directBuyPrompt(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Tap to view contact and request flow"
    AppLanguage.KANNADA -> "ಸಂಪರ್ಕ ಮತ್ತು ವಿನಂತಿ ವಿವರಗಳನ್ನು ನೋಡಲು ತಟ್ಟಿರಿ"
    AppLanguage.HINDI -> "संपर्क और अनुरोध विवरण देखने के लिए टैप करें"
}

private fun okLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "OK"
    AppLanguage.KANNADA -> "ಸರಿ"
    AppLanguage.HINDI -> "ठीक है"
}

private fun requestSentLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Request recorded"
    AppLanguage.KANNADA -> "ವಿನಂತಿ ದಾಖಲಿಸಲಾಗಿದೆ"
    AppLanguage.HINDI -> "अनुरोध दर्ज किया गया"
}

private fun backToFpoLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Back to FPO list"
    AppLanguage.KANNADA -> "FPO ಪಟ್ಟಿಗೆ ಹಿಂತಿರುಗಿ"
    AppLanguage.HINDI -> "FPO सूची पर वापस जाएँ"
}

private fun requestToBuyLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> "Request to Buy"
    AppLanguage.KANNADA -> "ಖರೀದಿ ವಿನಂತಿ"
    AppLanguage.HINDI -> "खरीद अनुरोध"
}

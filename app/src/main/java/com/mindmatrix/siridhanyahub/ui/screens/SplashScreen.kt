package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.AppText
import com.mindmatrix.siridhanyahub.ui.theme.CreamBackground
import com.mindmatrix.siridhanyahub.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    language: AppLanguage,
    onContinue: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isReady = viewModel.isReady.collectAsStateWithLifecycle()

    LaunchedEffect(isReady.value) {
        if (isReady.value) {
            delay(1500)
            onContinue()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = AppText.appTitle(language),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = AppText.splashTagline(language),
            style = MaterialTheme.typography.titleMedium
        )
        if (!isReady.value) {
            Text(
                text = "Preparing local app data...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

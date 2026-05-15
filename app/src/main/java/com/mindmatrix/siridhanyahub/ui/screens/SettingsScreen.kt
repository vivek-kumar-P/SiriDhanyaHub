package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.ui.components.SettingsRow
import com.mindmatrix.siridhanyahub.ui.i18n.AppLanguage
import com.mindmatrix.siridhanyahub.ui.i18n.AppText
import com.mindmatrix.siridhanyahub.ui.i18n.next
import com.mindmatrix.siridhanyahub.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    onAuth: () -> Unit,
    onEditProfile: () -> Unit,
    onAnalytics: () -> Unit,
    onAbout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppText.settingsTitle(language)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(onClick = { onLanguageChange(language.next()) }) {
                        Text(language.label)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (state.isLoggedIn) {
                        Text(
                            text = state.userName,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = state.userEmail,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        state.profile?.let { profile ->
                            Text(
                                text = "${profile.role.lowercase().replaceFirstChar(Char::titlecase)} | ${profile.district} | ${profile.contactNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    } else {
                        Text(AppText.guestMode(language), style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = AppText.guestModeDesc(language),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Person,
                    title = if (state.isLoggedIn) AppText.accountProfile(language) else AppText.loginSignup(language),
                    subtitle = if (state.isLoggedIn) AppText.accountProfileDesc(language) else AppText.loginSignupDesc(language),
                    onClick = if (state.isLoggedIn) onEditProfile else onAuth
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = AppText.language(language),
                    subtitle = AppText.languageDesc(language),
                    onClick = { onLanguageChange(language.next()) }
                )
            }
            if (state.showAnalytics) {
                item {
                    SettingsRow(
                        icon = Icons.Default.Analytics,
                        title = AppText.userAnalytics(language),
                        subtitle = AppText.userAnalyticsDesc(language),
                        onClick = onAnalytics
                    )
                }
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = AppText.about(language),
                    subtitle = AppText.aboutDesc(language),
                    onClick = onAbout
                )
            }
            if (state.isLoggedIn) {
                item {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = AppText.logout(language),
                        subtitle = AppText.logoutDesc(language),
                        onClick = viewModel::logout
                    )
                }
            } else {
                item {
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        title = AppText.browseMode(language),
                        subtitle = AppText.browseModeDesc(language),
                        onClick = {}
                    )
                }
            }
        }
    }
}

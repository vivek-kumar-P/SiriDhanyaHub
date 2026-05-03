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
import com.mindmatrix.siridhanyahub.ui.i18n.next
import com.mindmatrix.siridhanyahub.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    onAuth: () -> Unit,
    onAnalytics: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                    Text(
                        text = if (state.isLoggedIn) state.userName else "Guest mode",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = if (state.isLoggedIn) state.userEmail else "Login from here to unlock buy, saved, and analytics.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    state.profile?.let { profile ->
                        Text(
                            text = "${profile.role.lowercase().replaceFirstChar(Char::titlecase)} • ${profile.district} • ${profile.primaryMillet}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Person,
                    title = if (state.isLoggedIn) "Account profile" else "Login / Signup",
                    subtitle = if (state.isLoggedIn) "Manage your account details and saved profile." else "Authenticate to unlock gated features.",
                    onClick = onAuth
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Analytics,
                    title = "User analytics",
                    subtitle = if (state.isLoggedIn) "View transaction trends and your recent activity." else "Login required before opening analytics.",
                    onClick = onAnalytics
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = "Current: ${language.label}",
                    onClick = { onLanguageChange(language.next()) }
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "A millet discovery and trading companion for farmers and consumers.",
                    onClick = {}
                )
            }
            if (state.isLoggedIn) {
                item {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Logout",
                        subtitle = "Return to browse mode while keeping the app open.",
                        onClick = viewModel::logout
                    )
                }
            } else {
                item {
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        title = "Browse mode",
                        subtitle = "You can continue using market, recipe, and health sections without logging in.",
                        onClick = {}
                    )
                }
            }
        }
    }
}

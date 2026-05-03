package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.viewmodel.ProfileSetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    role: UserRole,
    onBack: () -> Unit,
    onAuthRequired: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(role) {
        viewModel.primeRole(role)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (role == UserRole.FARMER) "Farmer setup" else "Consumer setup") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = if (role == UserRole.FARMER)
                    "Tell us a few basics so the app can personalize sell-side guidance and analytics."
                else
                    "Tell us a few basics so the app can personalize buy-side suggestions and analytics.",
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                value = state.draft.name,
                onValueChange = viewModel::updateName,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") }
            )
            OutlinedTextField(
                value = state.draft.phone,
                onValueChange = viewModel::updatePhone,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone") }
            )
            OutlinedTextField(
                value = state.draft.district,
                onValueChange = viewModel::updateDistrict,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("District / City") }
            )
            OutlinedTextField(
                value = state.draft.primaryMillet,
                onValueChange = viewModel::updatePrimaryMillet,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(if (role == UserRole.FARMER) "Primary millet to sell" else "Primary millet to buy") }
            )
            state.message?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            Button(
                onClick = {
                    if (state.isLoggedIn) {
                        viewModel.save { success ->
                            if (success) onSaved()
                        }
                    } else {
                        onAuthRequired()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                Text(if (state.isLoggedIn) "Save profile" else "Login to save profile")
            }
        }
    }
}

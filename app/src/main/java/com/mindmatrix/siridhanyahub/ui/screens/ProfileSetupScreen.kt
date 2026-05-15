package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.profile.KarnatakaProfileData
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.viewmodel.ProfileSetupViewModel
import androidx.compose.foundation.layout.ColumnScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleConfirmationScreen(
    onBack: () -> Unit,
    onAuthRequired: (UserRole) -> Unit,
    onConfirmed: (UserRole) -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedRoleValue by rememberSaveable { mutableStateOf(state.activeRole?.value) }
    val selectedRole = UserRole.fromValue(selectedRoleValue)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose account type") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            if (state.isLoggedIn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            selectedRole?.let { role ->
                                viewModel.confirmRole(role) { success ->
                                    if (success) onConfirmed(role)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving && selectedRole != null
                    ) {
                        Text(if (state.isSaving) "Locking account type..." else "Confirm permanent account type")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Choose whether this account will permanently act as a farmer or a consumer.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Warning: once confirmed, this account type cannot be changed later. You can edit details in Settings, but you cannot switch between farmer and consumer.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            OutlinedButton(
                onClick = {
                    if (state.isLoggedIn) {
                        selectedRoleValue = UserRole.FARMER.value
                    } else {
                        onAuthRequired(UserRole.FARMER)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                border = if (selectedRole == UserRole.FARMER) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                }
            ) {
                Text(if (selectedRole == UserRole.FARMER) "Farmer selected" else "Farmer account")
            }
            OutlinedButton(
                onClick = {
                    if (state.isLoggedIn) {
                        selectedRoleValue = UserRole.CONSUMER.value
                    } else {
                        onAuthRequired(UserRole.CONSUMER)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                border = if (selectedRole == UserRole.CONSUMER) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                }
            ) {
                Text(if (selectedRole == UserRole.CONSUMER) "Consumer selected" else "Consumer account")
            }
            state.message?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (!state.isLoggedIn) {
                Text(
                    text = "Choosing a role takes you to account creation first. After signup, you will continue to the matching profile form.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val taluks = KarnatakaProfileData.taluksForDistrict(state.draft.district)
    val villages = KarnatakaProfileData.villagesForTaluk(state.draft.taluk)

    ProfileFormScaffold(
        title = if (state.isProfileComplete) "Edit farmer details" else "Farmer onboarding",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Fill this farmer profile once to activate your farmer account. Later, you can edit these details from Settings.",
                    style = MaterialTheme.typography.bodyLarge
                )
                FarmerCommonFields(state, taluks, villages, viewModel)
                OutlinedTextField(
                    value = state.draft.pmKisanOrFarmerId,
                    onValueChange = viewModel::updatePmKisanOrFarmerId,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("PM Kisan ID / Farmer ID") },
                    placeholder = { Text("e.g. PMKISAN1234567890") },
                    supportingText = {
                        Text("Enter your PM-KISAN Beneficiary ID (PMKISAN + 10 digits) or Karnataka Farmer ID (KA + 8-10 digits)")
                    },
                    isError = state.draft.pmKisanOrFarmerId.isNotBlank() &&
                            !state.draft.pmKisanOrFarmerId.trim().matches(
                                Regex("^(PMKISAN\\d{10}|KA\\d{8,10})$", RegexOption.IGNORE_CASE)
                            ),
                    singleLine = true
                )
                SelectorField(
                    label = "Primary millet category",
                    value = state.draft.primaryMilletCategory,
                    options = KarnatakaProfileData.milletTypes,
                    onValueSelected = viewModel::updatePrimaryMilletCategory
                )
                OutlinedTextField(
                    value = state.draft.landSizeAcres,
                    onValueChange = viewModel::updateLandSizeAcres,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Land size in acres") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                MessageBlock(state.message)
            }
            Button(
                onClick = { viewModel.saveFarmer { success -> if (success) onSaved() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding(),
                enabled = !state.isSaving && state.farmerFormReady
            ) {
                Text(if (state.isSaving) "Saving..." else "Save farmer profile")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerProfileFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val taluks = KarnatakaProfileData.taluksForDistrict(state.draft.district)
    val villages = KarnatakaProfileData.villagesForTaluk(state.draft.taluk)

    ProfileFormScaffold(
        title = if (state.isProfileComplete) "Edit consumer details" else "Consumer onboarding",
        onBack = onBack
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Fill this consumer profile once to activate your consumer account. Later, you can edit these details from Settings.",
                    style = MaterialTheme.typography.bodyLarge
                )
                ConsumerCommonFields(state, taluks, villages, viewModel)
                SelectorField(
                    label = "Preferred purchase location",
                    value = state.draft.preferredPurchaseLocation,
                    options = KarnatakaProfileData.districts,
                    onValueSelected = viewModel::updatePreferredPurchaseLocation
                )
                MessageBlock(state.message)
            }
            Button(
                onClick = { viewModel.saveConsumer { success -> if (success) onSaved() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding(),
                enabled = !state.isSaving && state.consumerFormReady
            ) {
                Text(if (state.isSaving) "Saving..." else "Save consumer profile")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileFormScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            content = content
        )
    }
}

@Composable
private fun FarmerCommonFields(
    state: com.mindmatrix.siridhanyahub.viewmodel.ProfileSetupUiState,
    taluks: List<String>,
    villages: List<String>,
    viewModel: ProfileSetupViewModel
) {
    CommonProfileFields(state, taluks, villages, viewModel)
}

@Composable
private fun ConsumerCommonFields(
    state: com.mindmatrix.siridhanyahub.viewmodel.ProfileSetupUiState,
    taluks: List<String>,
    villages: List<String>,
    viewModel: ProfileSetupViewModel
) {
    CommonProfileFields(state, taluks, villages, viewModel)
}

@Composable
private fun CommonProfileFields(
    state: com.mindmatrix.siridhanyahub.viewmodel.ProfileSetupUiState,
    taluks: List<String>,
    villages: List<String>,
    viewModel: ProfileSetupViewModel
) {
    OutlinedTextField(
        value = state.draft.fullName,
        onValueChange = viewModel::updateFullName,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Full name") },
        singleLine = true
    )
    OutlinedTextField(
        value = state.draft.contactNumber,
        onValueChange = viewModel::updateContactNumber,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Mobile number") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true
    )
    OutlinedTextField(
        value = state.draft.address,
        onValueChange = viewModel::updateAddress,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Address / place") }
    )
    SelectorField(
        label = "District",
        value = state.draft.district,
        options = KarnatakaProfileData.districts,
        onValueSelected = viewModel::updateDistrict
    )
    SelectorField(
        label = "Taluk",
        value = state.draft.taluk,
        options = taluks,
        enabled = state.draft.district.isNotBlank(),
        placeholder = if (state.draft.district.isBlank()) "Select district first" else "Choose taluk",
        onValueSelected = viewModel::updateTaluk
    )
    SelectorField(
        label = "Village / local area",
        value = state.draft.village,
        options = villages,
        enabled = state.draft.taluk.isNotBlank(),
        placeholder = if (state.draft.taluk.isBlank()) "Select taluk first" else "Choose village",
        onValueSelected = viewModel::updateVillage
    )
    OutlinedTextField(
        value = state.draft.aadhaarLast4,
        onValueChange = viewModel::updateAadhaarLast4,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Aadhaar last 4 digits") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
private fun MessageBlock(message: String?) {
    if (!message.isNullOrBlank()) {
        Text(
            text = message,
            color = if (message.contains("activated", ignoreCase = true) || message.contains("saved", ignoreCase = true)) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Box(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorField(
    label: String,
    value: String,
    options: List<String>,
    enabled: Boolean = true,
    placeholder: String = "Select an option",
    onValueSelected: (String) -> Unit
) {
    var expanded by rememberSaveable(label) { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            readOnly = true,
            enabled = enabled,
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

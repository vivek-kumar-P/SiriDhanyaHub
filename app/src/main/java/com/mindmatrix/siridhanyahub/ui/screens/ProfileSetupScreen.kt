package com.mindmatrix.siridhanyahub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.siridhanyahub.data.profile.KarnatakaProfileData
import com.mindmatrix.siridhanyahub.data.profile.UserRole
import com.mindmatrix.siridhanyahub.viewmodel.ProfileSetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onAuthRequired: () -> Unit,
    onSaved: (Boolean) -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val role = state.activeRole
    val scrollState = rememberScrollState()
    val isFormReadyToSubmit = when (role) {
        UserRole.FARMER -> state.draft.fullName.trim().length >= 3
                && state.draft.contactNumber.length == 10
                && state.draft.address.trim().isNotEmpty()
                && state.draft.district.isNotBlank()
                && state.draft.taluk.isNotBlank()
                && state.draft.village.isNotBlank()
                && state.draft.aadhaarLast4.length == 4
                && state.draft.pmKisanOrFarmerId.trim().isNotEmpty()
                && state.draft.primaryMilletCategory.isNotBlank()
                && (state.draft.landSizeAcres.toDoubleOrNull() ?: 0.0) > 0
        UserRole.CONSUMER -> state.draft.fullName.trim().length >= 3
                && state.draft.contactNumber.length == 10
                && state.draft.address.trim().isNotEmpty()
                && state.draft.district.isNotBlank()
                && state.draft.taluk.isNotBlank()
                && state.draft.village.isNotBlank()
                && state.draft.aadhaarLast4.length == 4
                && state.draft.preferredPurchaseLocation.trim().isNotEmpty()
        null -> false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (state.isLoggedIn) {
                            viewModel.save { success ->
                                if (success) {
                                    onSaved(state.activeRole == UserRole.FARMER)
                                }
                            }
                        } else {
                            onAuthRequired()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving && isFormReadyToSubmit
                ) {
                    Text(
                        if (state.isSaving) "Saving..."
                        else if (!state.isLoggedIn) "Login to Continue"
                        else "Submit / Confirm Profile"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Step 1: Choose your account type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.primeRole(UserRole.FARMER) },
                    modifier = Modifier.fillMaxWidth(),
                    border = if (role == UserRole.FARMER) 
                        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(if (role == UserRole.FARMER) "✓ Selected: I am a Farmer" else "I am a Farmer")
                }
                OutlinedButton(
                    onClick = { viewModel.primeRole(UserRole.CONSUMER) },
                    modifier = Modifier.fillMaxWidth(),
                    border = if (role == UserRole.CONSUMER) 
                        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(if (role == UserRole.CONSUMER) "✓ Selected: I am a Consumer" else "I am a Consumer")
                }
            }

            if (role != null) {
                Text(
                    "Step 2: Basic Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = state.draft.fullName,
                    onValueChange = viewModel::updateFullName,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full Name *") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.draft.contactNumber,
                    onValueChange = viewModel::updateContactNumber,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mobile Number (10 digits) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.draft.address,
                    onValueChange = viewModel::updateAddress,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Address / Place *") }
                )

                LocationDropdown(
                    label = "District *",
                    selectedOption = state.draft.district,
                    options = KarnatakaProfileData.districts,
                    onOptionSelected = viewModel::updateDistrict
                )

                LocationDropdown(
                    label = "Taluk *",
                    selectedOption = state.draft.taluk,
                    options = KarnatakaProfileData.taluksForDistrict(state.draft.district),
                    onOptionSelected = viewModel::updateTaluk,
                    enabled = state.draft.district.isNotBlank()
                )

                LocationDropdown(
                    label = "Village *",
                    selectedOption = state.draft.village,
                    options = KarnatakaProfileData.villagesForTaluk(state.draft.taluk),
                    onOptionSelected = viewModel::updateVillage,
                    enabled = state.draft.taluk.isNotBlank()
                )

                OutlinedTextField(
                    value = state.draft.aadhaarLast4,
                    onValueChange = viewModel::updateAadhaarLast4,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Aadhaar Reference (Last 4 digits) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                if (role == UserRole.FARMER) {
                    Text(
                        "Step 3: Farmer Specific Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.draft.pmKisanOrFarmerId,
                        onValueChange = viewModel::updatePmKisanOrFarmerId,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("PM Kisan ID / Farmer ID *") },
                        singleLine = true
                    )

                    LocationDropdown(
                        label = "Primary Millet Category *",
                        selectedOption = state.draft.primaryMilletCategory,
                        options = KarnatakaProfileData.milletTypes,
                        onOptionSelected = viewModel::updatePrimaryMilletCategory
                    )

                    OutlinedTextField(
                        value = state.draft.landSizeAcres,
                        onValueChange = viewModel::updateLandSizeAcres,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Land Size (in Acres) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }

                if (role == UserRole.CONSUMER) {
                    Text(
                        "Step 3: Consumer Preferences",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.draft.preferredPurchaseLocation,
                        onValueChange = viewModel::updatePreferredPurchaseLocation,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Preferred Purchase Location *") },
                        singleLine = true
                    )
                }

                state.message?.let {
                    Text(
                        text = it,
                        color = if (it.startsWith("Profile saved"))
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

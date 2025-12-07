package com.example.kiram.ui.screens.landlord

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kiram.ui.components.*
import com.example.kiram.util.Result

/**
 * Add Property Screen for Landlords
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    landlordId: String,
    onNavigateBack: () -> Unit,
    onPropertyAdded: () -> Unit,
    viewModel: AddPropertyViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val address by viewModel.address.collectAsState()
    val city by viewModel.city.collectAsState()
    val district by viewModel.district.collectAsState()
    val propertyType by viewModel.propertyType.collectAsState()
    val roomCount by viewModel.roomCount.collectAsState()
    val squareMeters by viewModel.squareMeters.collectAsState()
    val floor by viewModel.floor.collectAsState()
    val buildingAge by viewModel.buildingAge.collectAsState()
    val rentAmount by viewModel.rentAmount.collectAsState()
    val depositAmount by viewModel.depositAmount.collectAsState()
    val isFurnished by viewModel.isFurnished.collectAsState()
    val selectedFeatures by viewModel.selectedFeatures.collectAsState()
    val selectedPhotos by viewModel.selectedPhotos.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    
    val scrollState = rememberScrollState()
    
    // Photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { viewModel.addPhoto(it) }
    }
    
    // Handle save state
    LaunchedEffect(saveState) {
        when (saveState) {
            is Result.Success -> {
                onPropertyAdded()
                viewModel.resetSaveState()
            }
            is Result.Error -> {
                // Error will be shown in UI
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ev Ekle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Info Section
            SectionHeader(title = "Temel Bilgiler")
            
            KiramTextField(
                value = title,
                onValueChange = viewModel::updateTitle,
                label = "Başlık",
                placeholder = "Örn: Merkezi Konumda 2+1 Daire"
            )
            
            KiramTextField(
                value = description,
                onValueChange = viewModel::updateDescription,
                label = "Açıklama",
                placeholder = "Ev hakkında detaylı bilgi",
                singleLine = false
            )
            
            // Location Section
            SectionHeader(title = "Konum")
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KiramTextField(
                    value = city,
                    onValueChange = viewModel::updateCity,
                    label = "Şehir",
                    modifier = Modifier.weight(1f)
                )
                KiramTextField(
                    value = district,
                    onValueChange = viewModel::updateDistrict,
                    label = "İlçe",
                    modifier = Modifier.weight(1f)
                )
            }
            
            KiramTextField(
                value = address,
                onValueChange = viewModel::updateAddress,
                label = "Adres",
                placeholder = "Tam adres",
                singleLine = false
            )
            
            // Property Details Section
            SectionHeader(title = "Ev Detayları")
            
            // Property Type Dropdown
            PropertyTypeDropdown(
                selectedType = propertyType,
                onTypeSelected = viewModel::updatePropertyType
            )
            
            // Room Count Dropdown
            RoomCountDropdown(
                selectedRoomCount = roomCount,
                onRoomCountSelected = viewModel::updateRoomCount
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KiramTextField(
                    value = squareMeters,
                    onValueChange = viewModel::updateSquareMeters,
                    label = "Metrekare",
                    placeholder = "m²",
                    modifier = Modifier.weight(1f)
                )
                KiramTextField(
                    value = floor,
                    onValueChange = viewModel::updateFloor,
                    label = "Kat",
                    modifier = Modifier.weight(1f)
                )
                KiramTextField(
                    value = buildingAge,
                    onValueChange = viewModel::updateBuildingAge,
                    label = "Bina Yaşı",
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Financial Section
            SectionHeader(title = "Fiyat Bilgileri")
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KiramTextField(
                    value = rentAmount,
                    onValueChange = viewModel::updateRentAmount,
                    label = "Aylık Kira (₺)",
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
                KiramTextField(
                    value = depositAmount,
                    onValueChange = viewModel::updateDepositAmount,
                    label = "Depozito (₺)",
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Features Section
            SectionHeader(title = "Özellikler")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Eşyalı")
                Switch(
                    checked = isFurnished,
                    onCheckedChange = viewModel::updateIsFurnished
                )
            }
            
            FeatureChips(
                selectedFeatures = selectedFeatures,
                onFeatureToggle = viewModel::toggleFeature
            )
            
            // Photos Section
            SectionHeader(title = "Fotoğraflar")
            
            KiramPrimaryButton(
                text = "Fotoğraf Ekle (${selectedPhotos.size})",
                onClick = { photoPickerLauncher.launch("image/*") }
            )
            
            // Error message
            if (saveState is Result.Error) {
                Text(
                    text = (saveState as Result.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Save Button
            KiramPrimaryButton(
                text = "Kaydet",
                onClick = { viewModel.saveProperty(landlordId) },
                loading = saveState is Result.Loading,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertyTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val types = listOf("Daire", "Müstakil Ev", "Villa", "Stüdyo", "Rezidans")
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            label = { Text("Ev Tipi") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomCountDropdown(
    selectedRoomCount: String,
    onRoomCountSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val roomCounts = listOf("1+0", "1+1", "2+1", "3+1", "4+1", "5+1")
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedRoomCount,
            onValueChange = {},
            label = { Text("Oda Sayısı") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roomCounts.forEach { count ->
                DropdownMenuItem(
                    text = { Text(count) },
                    onClick = {
                        onRoomCountSelected(count)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FeatureChips(
    selectedFeatures: Set<String>,
    onFeatureToggle: (String) -> Unit
) {
    val features = listOf(
        "Balkon", "Asansör", "Otopark", "Güvenlik",
        "Havuz", "Spor Salonu", "Bahçe", "Teras"
    )
    
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        features.forEach { feature ->
            FilterChip(
                selected = selectedFeatures.contains(feature),
                onClick = { onFeatureToggle(feature) },
                label = { Text(feature) }
            )
        }
    }
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    // Simple implementation - in production use accompanist FlowRow
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        content()
    }
}

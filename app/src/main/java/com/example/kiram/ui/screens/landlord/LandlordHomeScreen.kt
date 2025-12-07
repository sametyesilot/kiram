package com.example.kiram.ui.screens.landlord

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kiram.data.model.Property
import com.example.kiram.data.repository.PropertyRepository
import com.example.kiram.ui.components.*
import com.example.kiram.util.Result
import com.example.kiram.util.toTurkishLira
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Landlord Home Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandlordHomeScreen(
    userId: String,
    onNavigateToAddProperty: () -> Unit,
    onNavigateToPropertyManagement: (String) -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDisputes: () -> Unit,
    viewModel: LandlordHomeViewModel = viewModel { LandlordHomeViewModel(userId) }
) {
    val propertiesState by viewModel.propertiesState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProperties()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evlerim") },
                actions = {
                    IconButton(onClick = onNavigateToMessages) {
                        Icon(Icons.Default.Message, contentDescription = "Mesajlar")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProperty,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ev Ekle")
            }
        }
    ) { paddingValues ->
        when (val state = propertiesState) {
            is Result.Loading -> {
                LoadingOverlay(isLoading = true, modifier = Modifier.padding(paddingValues))
            }
            is Result.Error -> {
                EmptyState(
                    message = state.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(
                        message = "Henüz eklenen ev yok",
                        actionText = "Ev Ekle",
                        onAction = onNavigateToAddProperty,
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            SectionHeader(
                                title = "Tüm Evler (${state.data.size})",
                                actionText = "Anlaşmazlıklar",
                                onAction = onNavigateToDisputes
                            )
                        }
                        
                        items(state.data) { property ->
                            LandlordPropertyCard(
                                property = property,
                                onClick = { onNavigateToPropertyManagement(property.propertyId) }
                            )
                        }
                    }
                }
            }
            Result.Idle -> {
                // Initial idle state
            }
        }
    }
}

@Composable
private fun LandlordPropertyCard(
    property: Property,
    onClick: () -> Unit
) {
    KiramCard(onClick = onClick) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = property.address,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PropertyInfoChip(
                            icon = Icons.Default.Home,
                            text = "${property.roomCount} Oda"
                        )
                        PropertyInfoChip(
                            icon = Icons.Default.Business,
                            text = "${property.floor}. Kat"
                        )
                    }
                }
                
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kira",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = property.rentAmount.toTurkishLira(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (property.currentTenantId != null) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ) {
                    Text(
                        text = if (property.currentTenantId != null) "Dolu" else "Boş",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
            Text(text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ViewModel
class LandlordHomeViewModel(
    private val userId: String,
    private val propertyRepository: PropertyRepository = PropertyRepository()
) : ViewModel() {
    
    private val _propertiesState = MutableStateFlow<Result<List<Property>>>(Result.Idle)
    val propertiesState: StateFlow<Result<List<Property>>> = _propertiesState
    
    fun loadProperties() {
        viewModelScope.launch {
            _propertiesState.value = propertyRepository.getPropertiesByLandlord(userId)
        }
    }
}

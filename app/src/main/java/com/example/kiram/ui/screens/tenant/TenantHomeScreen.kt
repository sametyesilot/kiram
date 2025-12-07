package com.example.kiram.ui.screens.tenant

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Tenant Home Screen - Shows available properties
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantHomeScreen(
    userId: String,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: TenantHomeViewModel = viewModel()
) {
    val propertyState by viewModel.propertyState.collectAsState()
    
    LaunchedEffect(userId) {
        viewModel.loadAvailableProperties()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kiralık Evler") },
                actions = {
                    IconButton(onClick = onNavigateToMessages) {
                        Icon(Icons.Default.Email, "Mesajlar")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, "Profil")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = propertyState) {
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
                val properties = state.data
                
                if (properties.isEmpty()) {
                    EmptyState(
                        message = "Henüz kiralık ev bulunmuyor",
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
                            Text(
                                text = "${properties.size} Kiralık Ev",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        items(properties) { property ->
                            PropertyCard(
                                property = property,
                                onClick = { onNavigateToPropertyDetail(property.propertyId) }
                            )
                        }
                    }
                }
            }
            Result.Idle -> {
                // Initial state
            }
        }
    }
}

@Composable
private fun PropertyCard(
    property: Property,
    onClick: () -> Unit
) {
    KiramCard(onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            Text(
                text = property.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Location
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${property.district}, ${property.city}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Details
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = property.roomCount,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (property.squareMeters > 0) {
                    Text(
                        text = "${property.squareMeters}m²",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Price
            Text(
                text = property.rentAmount.toTurkishLira() + "/ay",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ViewModel
class TenantHomeViewModel(
    private val propertyRepository: PropertyRepository = PropertyRepository()
) : ViewModel() {
    
    private val _propertyState = MutableStateFlow<Result<List<Property>>>(Result.Idle)
    val propertyState: StateFlow<Result<List<Property>>> = _propertyState.asStateFlow()
    
    /**
     * Load all available properties for browsing
     */
    fun loadAvailableProperties() {
        viewModelScope.launch {
            _propertyState.value = Result.Loading
            _propertyState.value = propertyRepository.getAvailableProperties()
        }
    }
}

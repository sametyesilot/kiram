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
import com.example.kiram.data.model.User
import com.example.kiram.data.repository.PropertyRepository
import com.example.kiram.data.repository.UserRepository
import com.example.kiram.ui.components.*
import com.example.kiram.util.Result
import com.example.kiram.util.toFormattedDate
import com.example.kiram.util.toTurkishLira
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Tenant Home Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantHomeScreen(
    userId: String,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: TenantHomeViewModel = viewModel { TenantHomeViewModel(userId) }
) {
    val propertyState by viewModel.propertyState.collectAsState()
    val landlordState by viewModel.landlordState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ana Sayfa") },
                actions = {
                    IconButton(onClick = onNavigateToMessages) {
                        Icon(Icons.Default.Message, contentDescription = "Mesajlar")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            propertyState is Result.Loading -> {
                LoadingOverlay(isLoading = true, modifier = Modifier.padding(paddingValues))
            }
            propertyState is Result.Error -> {
                EmptyState(
                    message = (propertyState as Result.Error).message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            propertyState is Result.Success -> {
                val property = (propertyState as Result.Success).data
                if (property == null) {
                    EmptyState(
                        message = "Henüz bir evde kiracı değilsiniz",
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    val landlord = (landlordState as? Result.Success)?.data
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Current Property Card
                        item {
                            SectionHeader(title = "Ev Bilgilerim")
                        }
                        
                        item {
                            PropertyCard(
                                property = property,
                                onClick = { onNavigateToPropertyDetail(property.propertyId) }
                            )
                        }
                        
                        // Landlord Info Card
                        if (landlord != null) {
                            item {
                                SectionHeader(title = "Ev Sahibi")
                            }
                            
                            item {
                                LandlordInfoCard(
                                    landlord = landlord,
                                    property = property,
                                    onMessage = { /* Navigate to chat */ },
                                    onRate = {  /* Navigate to rating */ }
                                )
                            }
                        }
                        
                        // Quick Actions
                        item {
                            SectionHeader(title = "Hızlı İşlemler")
                        }
                        
                        item {
                            QuickActionsCard(
                                property = property,
                                onReportDamage = { /* Navigate to damage report */ },
                                onViewHistory = { /* Navigate to damage history */ }
                            )
                        }
                    }
                }
            }
            propertyState is Result.Idle -> {
                // Initial idle state
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
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons. Default.ChevronRight, contentDescription = null)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(icon = Icons.Default.Home, text = "${property.roomCount} Oda")
                InfoChip(icon = Icons.Default.Business, text = "${property.floor}. Kat")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Kira: ${property.rentAmount.toTurkishLira()}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LandlordInfoCard(
    landlord: User,
    property: Property,
    onMessage: () -> Unit,
    onRate: () -> Unit
) {
    KiramCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = landlord.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = landlord.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onMessage) {
                    Icon(Icons.Default.Message, contentDescription = "Mesaj Gönder")
                }
                IconButton(onClick = onRate) {
                    Icon(Icons.Default.Star, contentDescription = "Değerlendir")
                }
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    property: Property,
    onReportDamage: () -> Unit,
    onViewHistory: () -> Unit
) {
    KiramCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionButton(
                icon = Icons.Default.ReportProblem,
                text = "Hasar Bildir",
                onClick = onReportDamage
            )
            Divider()
            ActionButton(
                icon = Icons.Default.History,
                text = "Hasar Geçmişi",
                onClick = onViewHistory
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ViewModel
class TenantHomeViewModel(
    private val userId: String,
    private val propertyRepository: PropertyRepository = PropertyRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    
    private val _propertyState = MutableStateFlow<Result<Property?>>(Result.Idle)
    val propertyState: StateFlow<Result<Property?>> = _propertyState
    
    private val _landlordState = MutableStateFlow<Result<User?>>(Result.Idle)
    val landlordState: StateFlow<Result<User?>> = _landlordState
    
    fun loadData() {
        viewModelScope.launch {
            // Load tenant's property
            _propertyState.value = propertyRepository.getPropertyByTenant(userId)
            
            // Load landlord info
            val property = (_propertyState.value as? Result.Success)?.data
            if (property != null) {
                _landlordState.value = userRepository.getUserById(property.landlordId)
            }
        }
    }
}

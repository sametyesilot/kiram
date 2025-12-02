package com.example.kiram.ui.screens.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Building Manager Home Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerHomeScreen(
    userId: String,
    onNavigateToCreateAnnouncement: () -> Unit,
    onNavigateToMaintenanceFees: () -> Unit,
    onNavigateToBuildingIssues: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ManagerHomeViewModel = viewModel { ManagerHomeViewModel(userId) }
) {
    val propertiesState by viewModel.propertiesState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProperties()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yönetici Paneli") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Building Stats
            item {
                SectionHeader(title = "Bina İstatistikleri")
            }
            
            item {
                when (val state = propertiesState) {
                    is Result.Success -> {
                        BuildingStatsCard(
                            totalProperties = state.data.size,
                            occupiedProperties = state.data.count { it.currentTenantId != null }
                        )
                    }
                    else -> {
                        KiramCard {
                            Text("Yükleniyor...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            
            // Quick Actions
            item {
                SectionHeader(title = "Hızlı İşlemler")
            }
            
            item {
                QuickActionsGrid(
                    onCreateAnnouncement = onNavigateToCreateAnnouncement,
                    onManageFees = onNavigateToMaintenanceFees,
                    onManageIssues = onNavigateToBuildingIssues
                )
            }
            
            // Recent Activity (placeholder)
            item {
                SectionHeader(title = "Son Aktiviteler")
            }
            
            item {
                EmptyState(
                    message = "Son aktivite bulunmuyor",
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun BuildingStatsCard(
    totalProperties: Int,
    occupiedProperties: Int
) {
    KiramCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Home,
                label = "Toplam Daire",
                value = totalProperties.toString()
            )
            
            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp)
            )
            
            StatItem(
                icon = Icons.Default.People,
                label = "Dolu",
                value = occupiedProperties.toString()
            )
            
            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp)
            )
            
            StatItem(
                icon = Icons.Outlined.Home,
                label = "Boş",
                value = (totalProperties - occupiedProperties).toString()
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActionsGrid(
    onCreateAnnouncement: () -> Unit,
    onManageFees: () -> Unit,
    onManageIssues: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(
                icon = Icons.Default.Announcement,
                title = "Duyuru Yayınla",
                onClick = onCreateAnnouncement,
                modifier = Modifier.weight(1f)
            )
            
            ActionCard(
                icon = Icons.Default.Payment,
                title = "Aidat Yönetimi",
                onClick = onManageFees,
                modifier = Modifier.weight(1f)
            )
        }
        
        ActionCard(
            icon = Icons.Default.Build,
            title = "Arıza/Sorun Kayıtları",
            onClick = onManageIssues,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KiramCard(onClick = onClick, modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ViewModel
class ManagerHomeViewModel(
    private val userId: String,
    private val propertyRepository: PropertyRepository = PropertyRepository()
) : ViewModel() {
    
    private val _propertiesState = MutableStateFlow<Result<List<Property>>>(Result.Loading)
    val propertiesState: StateFlow<Result<List<Property>>> = _propertiesState
    
    fun loadProperties() {
        viewModelScope.launch {
            _propertiesState.value = propertyRepository.getPropertiesByManager(userId)
        }
    }
}

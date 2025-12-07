package com.example.kiram.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kiram.data.model.User
import com.example.kiram.data.model.UserRole
import com.example.kiram.ui.components.*
import com.example.kiram.util.Result
import com.example.kiram.util.toFormattedDate
import java.text.SimpleDateFormat
import java.util.*

/**
 * Profile Screen - Displays user information and settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val userState by viewModel.userState.collectAsState()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(userId) {
        viewModel.loadUserData(userId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (val state = userState) {
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
                ProfileContent(
                    user = state.data,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToSettings = onNavigateToSettings,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                )
            }
            Result.Idle -> {
                // Initial state
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        ProfileHeader(user = user)
        
        // Contact Info Card
        ContactInfoCard(user = user)
        
        // Role-Specific Stats
        RoleStatsCard(user = user)
        
        // Actions Section
        ActionsSection(
            onEditProfile = onNavigateToEditProfile,
            onSettings = onNavigateToSettings,
            onLogout = onLogout
        )
    }
}

@Composable
private fun ProfileHeader(user: User) {
    KiramCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Email
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Role Badge
            RoleBadge(role = user.role)
        }
    }
}

@Composable
private fun RoleBadge(role: UserRole) {
    val (text, color) = when (role) {
        UserRole.TENANT -> "Kiracı" to MaterialTheme.colorScheme.primary
        UserRole.LANDLORD -> "Ev Sahibi" to MaterialTheme.colorScheme.secondary
        UserRole.MANAGER -> "Yönetici" to MaterialTheme.colorScheme.tertiary
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ContactInfoCard(user: User) {
    KiramCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "İletişim Bilgileri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            InfoRow(
                icon = Icons.Default.Phone,
                label = "Telefon",
                value = user.phone
            )
            
            InfoRow(
                icon = Icons.Default.Badge,
                label = "Kullanıcı ID",
                value = user.userId
            )
            
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = "Üyelik Tarihi",
                value = user.createdAt.toFormattedDate()
            )
        }
    }
}

@Composable
private fun RoleStatsCard(user: User) {
    KiramCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when (user.role) {
                    UserRole.TENANT -> "Kiracı Bilgileri"
                    UserRole.LANDLORD -> "Ev Sahibi İstatistikleri"
                    UserRole.MANAGER -> "Yönetici Bilgileri"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            when (user.role) {
                UserRole.TENANT -> {
                    InfoRow(
                        icon = Icons.Default.Home,
                        label = "Durum",
                        value = "Aktif Kiracı"
                    )
                }
                UserRole.LANDLORD -> {
                    InfoRow(
                        icon = Icons.Default.Business,
                        label = "Toplam Ev",
                        value = "-" // Will be populated from properties
                    )
                }
                UserRole.MANAGER -> {
                    user.buildingId?.let {
                        InfoRow(
                            icon = Icons.Default.Apartment,
                            label = "Bina ID",
                            value = it
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ActionsSection(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "İşlemler",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        KiramCard(onClick = onEditProfile) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Profili Düzenle",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        KiramCard(onClick = onSettings) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Ayarlar",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Çıkış Yap",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

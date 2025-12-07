package com.example.kiram.ui.screens.tenant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kiram.data.model.Property
import com.example.kiram.data.model.User
import com.example.kiram.ui.components.*
import com.example.kiram.util.Result
import com.example.kiram.util.toTurkishLira

/**
 * Screen for displaying property details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMessage: (String) -> Unit, // landlordId
    viewModel: PropertyDetailViewModel = viewModel()
) {
    val propertyState by viewModel.propertyState.collectAsState()
    val landlordState by viewModel.landlordState.collectAsState()
    
    LaunchedEffect(propertyId) {
        viewModel.loadPropertyDetails(propertyId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ev Detayı") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = propertyState) {
            is Result.Loading -> LoadingOverlay(isLoading = true, modifier = Modifier.padding(paddingValues))
            is Result.Error -> EmptyState(message = state.message, modifier = Modifier.padding(paddingValues))
            is Result.Success -> {
                PropertyDetailContent(
                    property = state.data,
                    landlordState = landlordState,
                    onNavigateToMessage = onNavigateToMessage,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            Result.Idle -> {}
        }
    }
}

@Composable
private fun PropertyDetailContent(
    property: Property,
    landlordState: Result<User>,
    onNavigateToMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Image Gallery
        if (property.photos.isNotEmpty()) {
            ImageGallery(photos = property.photos)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = property.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${property.district}, ${property.city}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Text(
                    text = property.rentAmount.toTurkishLira(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Divider()
            
            // Key Features Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(icon = Icons.Default.Home, label = "Oda", value = property.roomCount)
                DetailItem(icon = Icons.Default.SquareFoot, label = "Alan", value = "${property.squareMeters}m²")
                DetailItem(icon = Icons.Default.Business, label = "Kat", value = property.floor.toString())
                DetailItem(icon = Icons.Default.CalendarToday, label = "Yaş", value = "${property.buildingAge}")
            }
            
            // Financial Info
            KiramCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Fiyat Bilgileri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aylık Kira")
                        Text(property.rentAmount.toTurkishLira(), fontWeight = FontWeight.Bold)
                    }
                    if (property.depositAmount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Depozito")
                            Text(property.depositAmount.toTurkishLira(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            // Description
            if (property.description.isNotEmpty()) {
                SectionHeader("Açıklama")
                Text(
                    text = property.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Features
            if (property.features.isNotEmpty()) {
                SectionHeader("Özellikler")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    property.features.forEach { feature ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = feature,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            
            // Landlord Info
             if (landlordState is Result.Success) {
                SectionHeader("Ev Sahibi")
                LandlordCard(
                    landlord = landlordState.data,
                    onMessage = { onNavigateToMessage(property.landlordId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageGallery(photos: List<String>) {
    val pagerState = rememberPagerState(pageCount = { photos.size })
    
    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
        HorizontalPager(state = pagerState) { page ->
            AsyncImage(
                model = photos[page],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        // Page Indicator
        if (photos.size > 1) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${photos.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
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
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun LandlordCard(
    landlord: User,
    onMessage: () -> Unit
) {
    KiramCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Placeholder
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = landlord.fullName.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = landlord.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ev Sahibi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            FilledTonalButton(onClick = onMessage) {
                Icon(Icons.Default.Email, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mesaj")
            }
        }
    }
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    // Basic implementation
    Row(modifier = modifier, horizontalArrangement = horizontalArrangement) {
        content()
    }
}

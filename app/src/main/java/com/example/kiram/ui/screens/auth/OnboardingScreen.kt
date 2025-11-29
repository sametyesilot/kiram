package com.example.kiram.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.kiram.ui.components.KiramPrimaryButton
import com.example.kiram.ui.components.KiramSecondaryButton
import com.example.kiram.util.Constants
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

/**
 * Onboarding screens (3 pages)
 */
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPage(
                page = page,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 12.dp else 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.small,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    ) {}
                }
            }
        }
        
        // Buttons
        if (pagerState.currentPage == 2) {
            KiramPrimaryButton(
                text = "Başlayalım",
                onClick = {
                    coroutineScope.launch {
                        // Mark onboarding as completed
                        context.dataStore.edit { preferences ->
                            preferences[booleanPreferencesKey(Constants.KEY_ONBOARDING_COMPLETED)] = true
                        }
                        onNavigateToLogin()
                    }
                }
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                KiramSecondaryButton(
                    text = "Atla",
                    onClick = {
                        coroutineScope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[booleanPreferencesKey(Constants.KEY_ONBOARDING_COMPLETED)] = true
                            }
                            onNavigateToLogin()
                        }
                    },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                
                KiramPrimaryButton(
                    text = "İleri",
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OnboardingPage(
    page: Int,
    modifier: Modifier = Modifier
) {
    val (title, description, icon) = when (page) {
        0 -> Triple(
            "Puan Sistemi",
            "Kiracı ve ev sahipleri birbirlerini değerlendirebilir. Temizlik, iletişim, ödeme düzeni gibi kriterlerde puan vererek gelecekteki ilişkilerde rehberlik sağlayın.",
            "⭐"
        )
        1 -> Triple(
            "Oylama ve Yorumlar",
            "Şeffaf bir kiralama deneyimi için anonim veya açık yorumlar yapın. Ev sahipleri ve kiracılar arasında güven oluşturun.",
            "💬"
        )
        2 -> Triple(
            "Apartman Yönetimi",
            "Apartman yöneticileri duyuru yayınlayabilir, aidat takibi yapabilir ve bina sorunlarını yönetebilir. Tüm iletişim tek platformda!",
            "🏢"
        )
        else -> Triple("", "", "")
    }
    
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.displayLarge,
            fontSize = 80.dp.value.toInt().sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

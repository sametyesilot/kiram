package com.example.kiram.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kiram.navigation.Screen
import com.example.kiram.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Splash Screen with logo animation
 */
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (String) -> Unit // Pass user role
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        
        // Check onboarding status and login status
        val dataStore = context.dataStore
        val preferences = dataStore.data.first()
        
        val onboardingCompleted = preferences[booleanPreferencesKey(Constants.KEY_ONBOARDING_COMPLETED)] ?: false
        val isLoggedIn = preferences[booleanPreferencesKey(Constants.KEY_IS_LOGGED_IN)] ?: false
        val userRole = preferences[stringPreferencesKey(Constants.KEY_USER_ROLE)] ?: ""
        
        when {
            !onboardingCompleted -> onNavigateToOnboarding()
            !isLoggedIn -> onNavigateToLogin()
            else -> onNavigateToHome(userRole)
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo placeholder - you can replace with actual logo
            Text(
                text = "KİRÂM",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 56.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)

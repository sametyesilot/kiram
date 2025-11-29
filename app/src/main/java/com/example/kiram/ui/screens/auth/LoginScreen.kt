package com.example.kiram.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kiram.ui.components.*
import com.example.kiram.util.Constants
import com.example.kiram.util.Result
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

/**
 * Login Screen
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: (String) -> Unit, // Pass user role
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val loginState by viewModel.loginState.collectAsState()
    
    // Handle login state
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Result.Success -> {
                val user = state.data
                // Save login info to DataStore
                context.dataStore.edit { preferences ->
                    preferences[booleanPreferencesKey(Constants.KEY_IS_LOGGED_IN)] = true
                    preferences[stringPreferencesKey(Constants.KEY_USER_ID)] = user.userId
                    preferences[stringPreferencesKey(Constants.KEY_USER_ROLE)] = user.role.name
                }
                onNavigateToHome(user.role.name)
            }
            is Result.Error -> {
                // Error will be shown in UI
            }
            Result.Loading -> {
                // Loading state
            }
        }
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Title
            Text(
                text = "KİRÂM",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Hoş Geldiniz",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Email Field
            KiramTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-posta",
                placeholder = "ornek@email.com",
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                isError = loginState is Result.Error,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Field
            KiramTextField(
                value = password,
                onValueChange = { password = it },
                label = "Şifre",
                placeholder = "******",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                isError = loginState is Result.Error,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Show error message
            if (loginState is Result.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (loginState as Result.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Forgot Password
            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Şifremi Unuttum")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Login Button
            KiramPrimaryButton(
                text = "Giriş Yap",
                onClick = {
                    viewModel.login(email.trim(), password)
                },
                loading = loginState is Result.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Register Button
            KiramSecondaryButton(
                text = "Hesap Oluştur",
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

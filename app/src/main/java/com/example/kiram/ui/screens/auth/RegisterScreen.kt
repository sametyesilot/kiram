package com.example.kiram.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kiram.data.model.UserRole
import com.example.kiram.ui.components.*
import com.example.kiram.util.Constants
import com.example.kiram.util.Result

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

/**
 * Registration Screen
 */
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (String) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.TENANT) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val registerState by viewModel.registerState.collectAsState()
    
    // Handle register state
    LaunchedEffect(registerState) {
        when (val state = registerState) {
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
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = "Hesap Oluştur",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Full Name
            KiramTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Ad Soyad",
                placeholder = "Ahmet Yılmaz",
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Phone
            KiramTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Telefon",
                placeholder = "5XX XXX XX XX",
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email
            KiramTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-posta",
                placeholder = "ornek@email.com",
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password
            KiramTextField(
                value = password,
                onValueChange = { password = it },
                label = "Şifre",
                placeholder = "En az 6 karakter",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Confirm Password
            KiramTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Şifre Tekrar",
                placeholder = "Şifrenizi tekrar girin",
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // User Role Selection
            Text(
                text = "Kullanıcı Tipi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Role Selection Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoleSelectionCard(
                    title = "Kiracı",
                    description = "Ev kiralayan kullanıcı",
                    isSelected = selectedRole == UserRole.TENANT,
                    onClick = { selectedRole = UserRole.TENANT }
                )
                
                RoleSelectionCard(
                    title = "Ev Sahibi",
                    description = "Ev kiralayan kullanıcı",
                    isSelected = selectedRole == UserRole.LANDLORD,
                    onClick = { selectedRole = UserRole.LANDLORD }
                )
                
                RoleSelectionCard(
                    title = "Apartman Yöneticisi",
                    description = "Bina yöneticisi",
                    isSelected = selectedRole == UserRole.MANAGER,
                    onClick = { selectedRole = UserRole.MANAGER }
                )
            }
            
            // Show error message
            if (registerState is Result.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (registerState as Result.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Register Button
            KiramPrimaryButton(
                text = "Kayıt Ol",
                onClick = {
                    viewModel.register(
                        fullName = fullName.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        password = password,
                        confirmPassword = confirmPassword,
                        role = selectedRole
                    )
                },
                loading = registerState is Result.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Already have account
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zaten hesabınız var mı?",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text("Giriş Yap")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoleSelectionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    KiramCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

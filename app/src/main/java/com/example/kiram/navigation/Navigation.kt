package com.example.kiram.navigation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kiram.data.model.UserRole
import com.example.kiram.ui.screens.auth.*
import com.example.kiram.ui.screens.tenant.TenantHomeScreen
import com.example.kiram.ui.screens.landlord.LandlordHomeScreen
import com.example.kiram.ui.screens.manager.ManagerHomeScreen
import com.example.kiram.KiramApplication
import com.example.kiram.util.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Main navigation graph for KİRÂM app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiramNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = { role ->
                    navigateToRoleHome(navController, role)
                }
            )
        }
        
        // Onboarding Screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onNavigateToHome = { role ->
                    navigateToRoleHome(navController, role)
                }
            )
        }
        
        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = { role ->
                    navigateToRoleHome(navController, role)
                }
            )
        }
        
        // Forgot Password Screen (placeholder)
        composable(Screen.ForgotPassword.route) {
            // TODO: Implement ForgotPasswordScreen
            navController.popBackStack()
        }
        
        // Tenant Home Screen
        composable(Screen.TenantHome.route) {
            LaunchedEffect(Unit) {
                val userId = getUserId(context)
                if (userId.isEmpty()) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            
            val userId = remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                userId.value = getUserId(context)
            }
            
            if (userId.value.isNotEmpty()) {
                TenantHomeScreen(
                    userId = userId.value,
                    onNavigateToPropertyDetail = { propertyId ->
                        navController.navigate(Screen.PropertyDetail.createRoute(propertyId))
                    },
                    onNavigateToMessages = {
                        navController.navigate(Screen.Messages.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
        }
        
        // Landlord Home Screen
        composable(Screen.LandlordHome.route) {
            val userId = remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                userId.value = getUserId(context)
            }
            
            if (userId.value.isNotEmpty()) {
                LandlordHomeScreen(
                    userId = userId.value,
                    onNavigateToAddProperty = {
                        navController.navigate(Screen.AddProperty.route)
                    },
                    onNavigateToPropertyManagement = { propertyId ->
                        navController.navigate(Screen.PropertyManagement.createRoute(propertyId))
                    },
                    onNavigateToMessages = {
                        navController.navigate(Screen.Messages.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToDisputes = {
                        navController.navigate(Screen.DisputeManagement.route)
                    }
                )
            }
        }
        
        // Manager Home Screen
        composable(Screen.ManagerHome.route) {
            val userId = remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                userId.value = getUserId(context)
            }
            
            if (userId.value.isNotEmpty()) {
                ManagerHomeScreen(
                    userId = userId.value,
                    onNavigateToCreateAnnouncement = {
                        navController.navigate(Screen.CreateAnnouncement.route)
                    },
                    onNavigateToMaintenanceFees = {
                        navController.navigate(Screen.MaintenanceFees.route)
                    },
                    onNavigateToBuildingIssues = {
                        navController.navigate(Screen.BuildingIssues.route)
                    },
                    onNavigateToMessages = {
                        navController.navigate(Screen.Messages.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
        }
        
        // Placeholder screens - TODO: Implement these screens
        composable(Screen.Profile.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Profil") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Profil ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.Messages.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Mesajlar") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Mesajlar ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.PropertyDetail.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Ev Detayı") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ev detayı ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.AddProperty.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Ev Ekle") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ev ekleme ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.PropertyManagement.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Ev Yönetimi") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ev yönetimi ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.DisputeManagement.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Anlaşmazlıklar") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Anlaşmazlıklar ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.CreateAnnouncement.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Duyuru Oluştur") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Duyuru oluşturma ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.MaintenanceFees.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Aidat Yönetimi") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aidat yönetimi ekranı yakında eklenecek")
                }
            }
        }
        
        composable(Screen.BuildingIssues.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Bina Sorunları") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bina sorunları ekranı yakında eklenecek")
                }
            }
        }
    }
}

/**
 * Navigate to appropriate home screen based on user role
 */
private fun navigateToRoleHome(navController: NavHostController, roleString: String) {
    val route = when (roleString) {
        UserRole.TENANT.name -> Screen.TenantHome.route
        UserRole.LANDLORD.name -> Screen.LandlordHome.route
        UserRole.MANAGER.name -> Screen.ManagerHome.route
        else -> Screen.Login.route
    }
    
    navController.navigate(route) {
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Get user ID from DataStore
 */
private suspend fun getUserId(context: Context): String {
    val dataStore = (context.applicationContext as KiramApplication).dataStore
    return dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(Constants.KEY_USER_ID)] ?: ""
    }.first()
}

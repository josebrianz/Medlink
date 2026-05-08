package com.example.medilink2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.medilink2.data.UserManager
import com.example.medilink2.data.local.SettingsManager
import com.example.medilink2.ui.screens.*
import com.example.medilink2.ui.theme.Medilink2Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

enum class Screen {
    Onboarding, Login, Home, Search, CreateAccount, Profile, EditProfile, Navigate, Notifications, PharmacyDetail, PharmacyDashboard
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Database and write a test value
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("test")
        myRef.setValue("Hello Firebase 🚀")

        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val isDarkMode by settingsManager.isDarkMode.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    
    val auth = FirebaseAuth.getInstance()
    var userRole by remember { mutableStateOf<UserManager.UserRole?>(null) }
    var isCheckingRole by remember { mutableStateOf(auth.currentUser != null) }

    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            UserManager.getUserRole { role ->
                userRole = role
                isCheckingRole = false
            }
        } else {
            isCheckingRole = false
        }
    }

    Medilink2Theme(darkTheme = isDarkMode) {
        if (isCheckingRole) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            var currentScreen by rememberSaveable { 
                mutableStateOf(
                    if (auth.currentUser != null) {
                        if (userRole == UserManager.UserRole.PHARMACY_OWNER) Screen.PharmacyDashboard else Screen.Home
                    } else Screen.Onboarding
                ) 
            }
            
            // Note: Since currentScreen is rememberSaveable, we might need to force update it when userRole changes if it was null initially
            LaunchedEffect(userRole) {
                if (auth.currentUser != null && userRole != null) {
                    currentScreen = if (userRole == UserManager.UserRole.PHARMACY_OWNER) Screen.PharmacyDashboard else Screen.Home
                }
            }

            var selectedPharmacyId by rememberSaveable { mutableStateOf<String?>(null) }
            var searchQuery by rememberSaveable { mutableStateOf<String?>(null) }
            var highlightedDrug by rememberSaveable { mutableStateOf<String?>(null) }

            when (currentScreen) {
                Screen.Onboarding -> OnboardingScreen(
                    onGetStarted = { currentScreen = Screen.CreateAccount },
                    onLogin = { currentScreen = Screen.Login }
                )
                Screen.Login -> LoginScreen(
                    onBackToOnboarding = { currentScreen = Screen.Onboarding },
                    onLoginSuccess = { 
                        UserManager.getUserRole { role ->
                            userRole = role
                            currentScreen = if (role == UserManager.UserRole.PHARMACY_OWNER) Screen.PharmacyDashboard else Screen.Home
                        }
                    },
                    onNavigateToSignUp = { currentScreen = Screen.CreateAccount },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        coroutineScope.launch { settingsManager.setDarkMode(!isDarkMode) }
                    }
                )
                Screen.CreateAccount -> CreateAccountScreen(
                    onBackToLogin = { currentScreen = Screen.Login },
                    onAccountCreated = { 
                        UserManager.getUserRole { role ->
                            userRole = role
                            currentScreen = if (role == UserManager.UserRole.PHARMACY_OWNER) Screen.PharmacyDashboard else Screen.Home
                        }
                    },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        coroutineScope.launch { settingsManager.setDarkMode(!isDarkMode) }
                    }
                )
                Screen.PharmacyDashboard -> PharmacyDashboardScreen(
                    onLogout = { 
                        auth.signOut()
                        userRole = null
                        currentScreen = Screen.Login 
                    },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        coroutineScope.launch { settingsManager.setDarkMode(!isDarkMode) }
                    }
                )
                Screen.Home -> HomeScreen(
                    onNavigateToSearch = { query ->
                        searchQuery = query
                        currentScreen = Screen.Search 
                    },
                    onNavigateToProfile = { currentScreen = Screen.Profile },
                    onNavigateToNavigate = { currentScreen = Screen.Navigate },
                    onNotificationClick = { currentScreen = Screen.Notifications },
                    onPharmacyClick = { id ->
                        selectedPharmacyId = id
                        highlightedDrug = null
                        currentScreen = Screen.PharmacyDetail
                    },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { 
                        coroutineScope.launch { settingsManager.setDarkMode(!isDarkMode) }
                    }
                )
                Screen.Navigate -> MapScreen(
                    destinationPharmacyId = selectedPharmacyId,
                    onNavigateToHome = { currentScreen = Screen.Home },
                    onNavigateToSearch = { query ->
                        searchQuery = query
                        currentScreen = Screen.Search
                    },
                    onNavigateToProfile = { currentScreen = Screen.Profile },
                    onNavigateToNavigate = { 
                        selectedPharmacyId = null
                        currentScreen = Screen.Navigate 
                    },
                    onNotificationClick = { currentScreen = Screen.Notifications },
                    onNavigateToPharmacy = { id ->
                        selectedPharmacyId = id
                        highlightedDrug = null
                        currentScreen = Screen.PharmacyDetail
                    },
                    isDarkMode = isDarkMode
                )
                Screen.Search -> SearchScreen(
                    initialQuery = searchQuery,
                    onNavigateToHome = { currentScreen = Screen.Home },
                    onNavigateToSearch = { currentScreen = Screen.Search },
                    onNavigateToProfile = { currentScreen = Screen.Profile },
                    onNavigateToNavigate = { currentScreen = Screen.Navigate },
                    onNotificationClick = { currentScreen = Screen.Notifications },
                    onNavigateToPharmacy = { id, drug ->
                        selectedPharmacyId = id
                        highlightedDrug = drug
                        currentScreen = Screen.PharmacyDetail
                    },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { 
                        coroutineScope.launch { settingsManager.setDarkMode(!isDarkMode) }
                    }
                )
                Screen.Profile -> ProfileScreen(
                    onNavigateToHome = { currentScreen = Screen.Home },
                    onNavigateToSearch = { currentScreen = Screen.Search },
                    onNavigateToNavigate = { currentScreen = Screen.Navigate },
                    onNavigateToEditProfile = { currentScreen = Screen.EditProfile },
                    onNotificationClick = { currentScreen = Screen.Notifications },
                    onLogout = { currentScreen = Screen.Login },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { 
                        coroutineScope.launch { settingsManager.setDarkMode(!isDarkMode) }
                    }
                )
                Screen.EditProfile -> EditProfileScreen(
                    onBack = { currentScreen = Screen.Profile },
                    onProfileUpdated = { currentScreen = Screen.Profile }
                )
                Screen.Notifications -> NotificationScreen(
                    onBack = { currentScreen = Screen.Home }
                )
                Screen.PharmacyDetail -> PharmacyDetailScreen(
                    pharmacyId = selectedPharmacyId ?: "1",
                    highlightedDrug = highlightedDrug,
                    onBack = { currentScreen = Screen.Search },
                    onNavigateToMap = { id ->
                        selectedPharmacyId = id
                        currentScreen = Screen.Navigate
                    },
                    onNotificationClick = { currentScreen = Screen.Notifications },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { 
                        coroutineScope.launch { settingsManager.setDarkMode(!isDarkMode) }
                    }
                )
            }
        }
    }
}

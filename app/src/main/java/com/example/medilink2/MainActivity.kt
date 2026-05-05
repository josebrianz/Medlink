package com.example.medilink2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medilink2.ui.screens.CreateAccountScreen
import com.example.medilink2.ui.screens.EditProfileScreen
import com.example.medilink2.ui.screens.HomeScreen
import com.example.medilink2.ui.screens.LoginScreen
import com.example.medilink2.ui.screens.MapScreen
import com.example.medilink2.ui.screens.OnboardingScreen
import com.example.medilink2.ui.screens.PharmacyDetailScreen
import com.example.medilink2.ui.screens.ProfileScreen
import com.example.medilink2.ui.screens.SearchScreen
import com.example.medilink2.ui.theme.Medilink2Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

enum class Screen {
    Onboarding, Login, Home, Search, CreateAccount, Profile, EditProfile, PharmacyDetail, Navigate, Notifications
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
            Medilink2Theme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val auth = FirebaseAuth.getInstance()
    var currentScreen by remember { 
        mutableStateOf(if (auth.currentUser != null) Screen.Home else Screen.Onboarding) 
    }
    var selectedPharmacyId by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    when (currentScreen) {
        Screen.Onboarding -> OnboardingScreen(
            onGetStarted = { currentScreen = Screen.CreateAccount },
            onLogin = { currentScreen = Screen.Login }
        )
        Screen.Login -> LoginScreen(
            onBackToOnboarding = { currentScreen = Screen.Onboarding },
            onLoginSuccess = { currentScreen = Screen.Home },
            onNavigateToSignUp = { currentScreen = Screen.CreateAccount }
        )
        Screen.CreateAccount -> CreateAccountScreen(
            onBackToLogin = { currentScreen = Screen.Login },
            onAccountCreated = { currentScreen = Screen.Home }
        )
        Screen.Home -> HomeScreen(
            onNavigateToSearch = { 
                searchQuery = ""
                currentScreen = Screen.Search 
            },
            onNavigateToProfile = { currentScreen = Screen.Profile },
            onNavigateToNotifications = { currentScreen = Screen.Notifications },
            onNavigateToNavigate = { 
                selectedPharmacyId = ""
                currentScreen = Screen.Navigate 
            },
            onNavigateToPharmacy = { id ->
                selectedPharmacyId = id
                currentScreen = Screen.PharmacyDetail
            }
        )
        Screen.Search -> SearchScreen(
            initialQuery = searchQuery,
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToPharmacy = { id ->
                selectedPharmacyId = id
                currentScreen = Screen.PharmacyDetail
            },
            onNavigateToNotifications = { currentScreen = Screen.Notifications },
            onNavigateToNavigate = {
                selectedPharmacyId = ""
                currentScreen = Screen.Navigate
            }
        )
        Screen.Profile -> ProfileScreen(
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToSearch = { 
                searchQuery = ""
                currentScreen = Screen.Search
            },
            onNavigateToEditProfile = { currentScreen = Screen.EditProfile },
            onLogout = { currentScreen = Screen.Login },
            onNavigateToNavigate = {
                selectedPharmacyId = ""
                currentScreen = Screen.Navigate
            }
        )
        Screen.EditProfile -> EditProfileScreen(
            onBack = { currentScreen = Screen.Profile },
            onProfileUpdated = { currentScreen = Screen.Profile }
        )
        Screen.PharmacyDetail -> PharmacyDetailScreen(
            pharmacyId = selectedPharmacyId,
            onBack = { currentScreen = Screen.Home },
            onNavigateToNotifications = { currentScreen = Screen.Notifications },
            onNavigateToNavigate = {
                currentScreen = Screen.Navigate
            }
        )
        Screen.Navigate -> MapScreen(
            destinationPharmacyId = selectedPharmacyId.takeIf { it.isNotEmpty() },
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToSearch = { query ->
                searchQuery = query ?: ""
                currentScreen = Screen.Search
            },
            onNavigateToProfile = { currentScreen = Screen.Profile },
            onNavigateToNavigate = { currentScreen = Screen.Navigate },
            onNavigateToPharmacy = { id ->
                selectedPharmacyId = id
                currentScreen = Screen.PharmacyDetail
            }
        )
        Screen.Notifications -> com.example.medilink2.ui.screens.NotificationScreen(
            onBack = { currentScreen = Screen.Home }
        )
    }
}

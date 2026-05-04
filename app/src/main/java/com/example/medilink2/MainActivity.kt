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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.medilink2.data.NotificationLogicManager
import com.example.medilink2.data.UserManager
import com.example.medilink2.ui.screens.*
import com.example.medilink2.ui.theme.Medilink2Theme
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

enum class Screen {
    Onboarding, Login, Home, Search, CreateAccount, PharmacyDetail, Notifications
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("test")
        myRef.setValue("Hello Firebase 🚀")

        // Update FCM token if user is logged in
        UserManager.updateFcmToken()

        // Check for drug availability notifications on startup
        val userId = UserManager.getUserId()
        if (userId != null) {
            lifecycleScope.launch {
                NotificationLogicManager.checkStockAndNotify(this@MainActivity, userId)
            }
        }

        setContent {
            Medilink2Theme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(Screen.Onboarding) }
    var searchQuery by remember { mutableStateOf<String?>(null) }
    var selectedPharmacyId by remember { mutableStateOf("1") }

    when (currentScreen) {
        Screen.Onboarding -> OnboardingScreen(
            onGetStarted = { currentScreen = Screen.CreateAccount },
        ) {
            currentScreen = Screen.Login
        }
        Screen.Login -> LoginScreen(
            onBackToOnboarding = { currentScreen = Screen.Onboarding },
            onLoginSuccess = { currentScreen = Screen.Home },
        ) {
            currentScreen = Screen.CreateAccount
        }
        Screen.CreateAccount -> CreateAccountScreen(
            onBackToLogin = { currentScreen = Screen.Login },
        ) {
            currentScreen = Screen.Login
        }
        Screen.Home -> HomeScreen(
            onNavigateToSearch = { query -> 
                searchQuery = query
                currentScreen = Screen.Search 
            },
            onNavigateToSeeAll = {
                searchQuery = "" // Reset query to show all pharmacies
                currentScreen = Screen.Search
            },
            onNavigateToPharmacy = { id -> 
                selectedPharmacyId = id
                currentScreen = Screen.PharmacyDetail 
            },
            onNavigateToNotifications = { currentScreen = Screen.Notifications },
        )
        Screen.Search -> SearchScreen(
            initialQuery = searchQuery,
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToPharmacy = { id -> 
                selectedPharmacyId = id
                currentScreen = Screen.PharmacyDetail 
            },
            onNavigateToNotifications = { currentScreen = Screen.Notifications },
        )
        Screen.PharmacyDetail -> PharmacyDetailScreen(
            pharmacyId = selectedPharmacyId,
            onBack = { currentScreen = Screen.Home },
            onNavigateToNotifications = { currentScreen = Screen.Notifications },
        )
        Screen.Notifications -> NotificationScreen(
            onBack = { currentScreen = Screen.Home }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    Medilink2Theme {
        MainApp()
    }
}

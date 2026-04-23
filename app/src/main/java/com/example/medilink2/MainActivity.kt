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
import com.example.medilink2.ui.screens.*
import com.example.medilink2.ui.theme.Medilink2Theme
import com.google.firebase.database.FirebaseDatabase

enum class Screen {
    Onboarding, Login, Home, Search, CreateAccount, PharmacyDetail
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Database
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
    var currentScreen by remember { mutableStateOf(Screen.Onboarding) }
    var searchQuery by remember { mutableStateOf<String?>(null) }

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
            onAccountCreated = { currentScreen = Screen.Login }
        )
        Screen.Home -> HomeScreen(
            onNavigateToSearch = { query -> 
                searchQuery = query
                currentScreen = Screen.Search 
            },
            onNavigateToSeeAll = {
                searchQuery = "" // Reset query to show all pharmacies
                currentScreen = Screen.Search
            },
            onNavigateToPharmacy = { currentScreen = Screen.PharmacyDetail }
        )
        Screen.Search -> SearchScreen(
            initialQuery = searchQuery,
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToPharmacy = { currentScreen = Screen.PharmacyDetail }
        )
        Screen.PharmacyDetail -> PharmacyDetailScreen(
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

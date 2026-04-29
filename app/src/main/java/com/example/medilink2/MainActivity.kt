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
import com.example.medilink2.ui.screens.*
import com.example.medilink2.ui.theme.Medilink2Theme
import com.google.firebase.database.FirebaseDatabase

enum class Screen {
    Onboarding, Login, Home, Search, CreateAccount, PharmacyDetail, Navigate
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
    var previousScreen by remember { mutableStateOf<Screen?>(null) }
    var searchQuery by remember { mutableStateOf<String?>(null) }
    var selectedPharmacyId by remember { mutableStateOf("1") }

    fun navigateTo(screen: Screen) {
        previousScreen = currentScreen
        currentScreen = screen
    }

    when (currentScreen) {
        Screen.Onboarding -> OnboardingScreen(
            onGetStarted = { navigateTo(Screen.CreateAccount) },
            onLogin = { navigateTo(Screen.Login) }
        )
        Screen.Login -> LoginScreen(
            onBackToOnboarding = { navigateTo(Screen.Onboarding) },
            onLoginSuccess = { navigateTo(Screen.Home) },
            onNavigateToSignUp = { navigateTo(Screen.CreateAccount) }
        )
        Screen.CreateAccount -> CreateAccountScreen(
            onBackToLogin = { navigateTo(Screen.Login) },
            onAccountCreated = { navigateTo(Screen.Login) }
        )
        Screen.Home -> HomeScreen(
            onNavigateToSearch = { query -> 
                searchQuery = query
                navigateTo(Screen.Search) 
            },
            onNavigateToSeeAll = {
                searchQuery = "" // Reset query to show all pharmacies
                navigateTo(Screen.Search)
            },
            onNavigateToPharmacy = { id ->
                selectedPharmacyId = id
                navigateTo(Screen.PharmacyDetail) 
            },
            onNavigateToNavigate = { navigateTo(Screen.Navigate) }
        )
        Screen.Search -> SearchScreen(
            initialQuery = searchQuery,
            onNavigateToHome = { navigateTo(Screen.Home) },
            onNavigateToSearch = { navigateTo(Screen.Search) },
            onNavigateToPharmacy = { id, drug ->
                selectedPharmacyId = id
                searchQuery = drug
                navigateTo(Screen.PharmacyDetail) 
            },
            onNavigateToNavigate = { navigateTo(Screen.Navigate) }
        )
        Screen.PharmacyDetail -> PharmacyDetailScreen(
            pharmacyId = selectedPharmacyId,
            highlightedDrug = if (searchQuery?.isNotEmpty() == true) searchQuery else null,
            onBack = { navigateTo(Screen.Home) },
            onNavigateToNavigate = { navigateTo(Screen.Navigate) }
        )
        Screen.Navigate -> MapScreen(
            destinationPharmacyId = if (previousScreen == Screen.PharmacyDetail) selectedPharmacyId else null,
            onNavigateToHome = { navigateTo(Screen.Home) },
            onNavigateToSearch = { query ->
                searchQuery = query
                navigateTo(Screen.Search)
            },
            onNavigateToNavigate = { /* Already here */ },
            onNavigateToPharmacy = { id ->
                selectedPharmacyId = id
                navigateTo(Screen.PharmacyDetail)
            }
        )
    }
}

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

enum class Screen {
    Onboarding, Home, Search, CreateAccount, PharmacyDetail
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

    when (currentScreen) {
        Screen.Onboarding -> OnboardingScreen(
            onGetStarted = { currentScreen = Screen.CreateAccount },
            onLogin = { currentScreen = Screen.Home }
        )
        Screen.CreateAccount -> CreateAccountScreen(
            onBackToLogin = { currentScreen = Screen.Onboarding }
        )
        Screen.Home -> HomeScreen(
            onNavigateToSearch = { currentScreen = Screen.Search },
            onNavigateToPharmacy = { currentScreen = Screen.PharmacyDetail }
        )
        Screen.Search -> SearchScreen(
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToPharmacy = { currentScreen = Screen.PharmacyDetail }
        )
        Screen.PharmacyDetail -> PharmacyDetailScreen(
            onBack = { currentScreen = Screen.Home }
        )
    }
}

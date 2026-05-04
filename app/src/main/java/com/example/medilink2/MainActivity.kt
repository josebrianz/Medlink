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
import com.example.medilink2.ui.screens.OnboardingScreen
import com.example.medilink2.ui.screens.ProfileScreen
import com.example.medilink2.ui.screens.SearchScreen
import com.example.medilink2.ui.theme.Medilink2Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

enum class Screen {
    Onboarding, Login, Home, Search, CreateAccount, Profile, EditProfile
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
            onNavigateToSearch = { currentScreen = Screen.Search },
            onNavigateToProfile = { currentScreen = Screen.Profile },
            onNavigateToNavigate = { /* currentScreen = Screen.Navigate */ }
        )
        Screen.Search -> SearchScreen(
            onNavigateToHome = { currentScreen = Screen.Home }
        )
        Screen.Profile -> ProfileScreen(
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToSearch = { currentScreen = Screen.Search },
            onNavigateToEditProfile = { currentScreen = Screen.EditProfile },
            onLogout = { currentScreen = Screen.Login }
        )
        Screen.EditProfile -> EditProfileScreen(
            onBack = { currentScreen = Screen.Profile },
            onProfileUpdated = { currentScreen = Screen.Profile }
        )
    }
}

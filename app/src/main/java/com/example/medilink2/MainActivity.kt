package com.example.medilink2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilink2.ui.screens.*
import com.example.medilink2.ui.theme.Medilink2Theme
import com.example.medilink2.ui.viewmodel.NavigationViewModel
import com.example.medilink2.ui.viewmodel.Screen
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Database
        FirebaseDatabase.getInstance().getReference("test").setValue("Hello Firebase 🚀")

        setContent {
            val navViewModel: NavigationViewModel = viewModel()
            Medilink2Theme(darkTheme = navViewModel.isDarkMode) {
                MainApp(navViewModel)
            }
        }
    }
}

@Composable
fun MainApp(navViewModel: NavigationViewModel = viewModel()) {
    val currentScreen = navViewModel.currentScreen
    val searchQuery = navViewModel.searchQuery
    val selectedPharmacyId = navViewModel.selectedPharmacyId

    when (currentScreen) {
        Screen.Onboarding -> OnboardingScreen(
            onGetStarted = { navViewModel.navigateTo(Screen.CreateAccount) },
            onLogin = { navViewModel.navigateTo(Screen.Login) },
            isDarkMode = navViewModel.isDarkMode,
            onToggleDarkMode = { navViewModel.toggleDarkMode() }
        )
        Screen.Login -> LoginScreen(
            onBackToOnboarding = { navViewModel.navigateTo(Screen.Onboarding) },
            onLoginSuccess = { navViewModel.navigateTo(Screen.Home) },
            onNavigateToSignUp = { navViewModel.navigateTo(Screen.CreateAccount) },
            isDarkMode = navViewModel.isDarkMode,
            onToggleDarkMode = { navViewModel.toggleDarkMode() }
        )
        Screen.CreateAccount -> CreateAccountScreen(
            onBackToLogin = { navViewModel.navigateTo(Screen.Login) },
            onAccountCreated = { navViewModel.navigateTo(Screen.Login) },
            isDarkMode = navViewModel.isDarkMode,
            onToggleDarkMode = { navViewModel.toggleDarkMode() }
        )
        Screen.Home -> HomeScreen(
            onNavigateToSearch = { query -> 
                navViewModel.navigateToSearch(query ?: "")
            },
            onNavigateToSeeAll = {
                navViewModel.navigateToSearch("") 
            },
            onNavigateToPharmacy = { pharmacyId: String -> 
                navViewModel.navigateToPharmacyDetail(pharmacyId)
            },
            isDarkMode = navViewModel.isDarkMode,
            onToggleDarkMode = { navViewModel.toggleDarkMode() }
        )
        Screen.Search -> SearchScreen(
            initialQuery = searchQuery,
            onNavigateToHome = { navViewModel.navigateTo(Screen.Home) },
            onNavigateToPharmacy = { pharmacyId: String, drugName: String? ->
                navViewModel.navigateToPharmacyDetail(pharmacyId, drugName)
            },
            isDarkMode = navViewModel.isDarkMode,
            onToggleDarkMode = { navViewModel.toggleDarkMode() }
        )
        Screen.PharmacyDetail -> PharmacyDetailScreen(
            pharmacyId = selectedPharmacyId ?: "1",
            highlightedDrug = navViewModel.searchedDrugName,
            onBack = { navViewModel.navigateTo(Screen.Search) },
            isDarkMode = navViewModel.isDarkMode,
            onToggleDarkMode = { navViewModel.toggleDarkMode() }
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

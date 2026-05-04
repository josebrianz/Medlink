package com.example.medilink2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Screen {
    Onboarding, Login, Home, Search, CreateAccount, PharmacyDetail
}

class NavigationViewModel : ViewModel() {
    var currentScreen by mutableStateOf(Screen.Onboarding)
        private set

    var isDarkMode by mutableStateOf(false)
        private set

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }

    var searchQuery by mutableStateOf<String?>(null)
        private set

    var selectedPharmacyId by mutableStateOf<String?>(null)
        private set

    var searchedDrugName by mutableStateOf<String?>(null)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun navigateToSearch(query: String = "") {
        searchQuery = query
        searchedDrugName = null // Reset when starting fresh search
        currentScreen = Screen.Search
    }

    fun navigateToPharmacyDetail(pharmacyId: String, drugName: String? = null) {
        selectedPharmacyId = pharmacyId
        searchedDrugName = drugName
        currentScreen = Screen.PharmacyDetail
    }
}

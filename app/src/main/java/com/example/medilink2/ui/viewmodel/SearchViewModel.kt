package com.example.medilink2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.medilink2.ui.screens.SearchResult

class SearchViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    val allResults = listOf(
        SearchResult(id = "1", name = "MedPlus Pharmacy", location = "Kampala Road, Plot 23", distance = "0.8 km", price = "UGX 3,000", rating = "4.8", closingTime = "9:00 PM", inStock = true, tags = listOf("Paracetamol", "Panadol Extra", "Diclofenac Gel", "Pain Relief", "Gaviscon")),
        SearchResult(id = "2", name = "City Chemist", location = "Jinja Road, Near Total", distance = "1.2 km", price = "UGX 2,500", rating = "4.5", closingTime = "8:00 PM", inStock = true, tags = listOf("Paracetamol", "Amoxicillin", "Vitamin C", "Fever", "Augustin")),
        SearchResult(id = "3", name = "HealthGuard Pharmacy", location = "Nasser Road, Block B", distance = "1.8 km", price = "UGX 3,500", rating = "4.2", closingTime = "10:00 PM", inStock = true, tags = listOf("Aspirin 81mg", "Atorvastatin", "Lisinopril", "Heart", "Amlodipine")),
        SearchResult(id = "4", name = "QuickMeds", location = "Bombo Road, Wandegeya", distance = "3.1 km", price = "UGX 2,800", rating = "4.6", closingTime = "7:00 PM", inStock = false, tags = listOf("Insulin Glargine", "Metformin", "Diabetes")),
        SearchResult(id = "5", name = "Allergy Care", location = "Wandegeya Market", distance = "2.0 km", price = "UGX 4,000", rating = "4.4", closingTime = "6:00 PM", inStock = true, tags = listOf("Cetirizine", "Loratadine", "Allergy", "Piriton")),
        SearchResult(id = "6", name = "General Wellness", location = "Mulago Hill", distance = "2.5 km", price = "UGX 1,500", rating = "4.1", closingTime = "11:00 PM", inStock = true, tags = listOf("ORS Sachet", "Salbutamol Inhaler", "Omeprazole", "General", "Folic Acid")),
        SearchResult(id = "7", name = "First Care Pharmacy", location = "Kikuubo Lane", distance = "0.5 km", price = "UGX 2,200", rating = "4.7", closingTime = "11:00 PM", inStock = true, tags = listOf("Paracetamol", "Amoxicillin", "Metronidazole", "Antibiotic")),
        SearchResult(id = "8", name = "Eco Pharmacy", location = "Kisementi", distance = "2.2 km", price = "UGX 5,500", rating = "4.9", closingTime = "12:00 AM", inStock = true, tags = listOf("Vitamin C", "Folic Acid", "Supplements", "Gaviscon")),
        SearchResult(id = "9", name = "Vine Pharmacy", location = "Lugogo Mall", distance = "3.5 km", price = "UGX 35,000", rating = "4.6", closingTime = "10:00 PM", inStock = true, tags = listOf("Gaviscon", "Ventolin", "General")),
        SearchResult(id = "10", name = "Family Health Pharmacy", location = "Ntinda Road", distance = "4.1 km", price = "UGX 10,000", rating = "4.3", closingTime = "9:30 PM", inStock = true, tags = listOf("Durex Condoms", "General", "Loratadine")),
    )

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun setInitialQuery(query: String?) {
        searchQuery = query ?: ""
    }

    fun getFilteredResults(): List<SearchResult> {
        return if (searchQuery.isEmpty()) {
            allResults
        } else {
            allResults.filter { result ->
                result.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
                result.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
}

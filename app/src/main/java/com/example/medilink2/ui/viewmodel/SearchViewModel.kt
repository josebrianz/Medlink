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
        SearchResult("MedPlus Pharmacy", "Kampala Road, Plot 23", "0.8 km", "UGX 3,000", "4.8", "9:00 PM", true, tags = listOf("Paracetamol", "Panadol Extra", "Diclofenac Gel", "Pain Relief", "Gaviscon")),
        SearchResult("City Chemist", "Jinja Road, Near Total", "1.2 km", "UGX 2,500", "4.5", "8:00 PM", true, tags = listOf("Paracetamol", "Amoxicillin", "Vitamin C", "Fever", "Augustin")),
        SearchResult("HealthGuard Pharmacy", "Nasser Road, Block B", "1.8 km", "UGX 3,500", "4.2", "10:00 PM", true, tags = listOf("Aspirin 81mg", "Atorvastatin", "Lisinopril", "Heart", "Amlodipine")),
        SearchResult("QuickMeds", "Bombo Road, Wandegeya", "3.1 km", "UGX 2,800", "4.6", "7:00 PM", false, tags = listOf("Insulin Glargine", "Metformin", "Diabetes")),
        SearchResult("Allergy Care", "Wandegeya Market", "2.0 km", "UGX 4,000", "4.4", "6:00 PM", true, tags = listOf("Cetirizine", "Loratadine", "Allergy", "Piriton")),
        SearchResult("General Wellness", "Mulago Hill", "2.5 km", "UGX 1,500", "4.1", "11:00 PM", true, tags = listOf("ORS Sachet", "Salbutamol Inhaler", "Omeprazole", "General", "Folic Acid")),
        SearchResult("First Care Pharmacy", "Kikuubo Lane", "0.5 km", "UGX 2,200", "4.7", "11:00 PM", true, tags = listOf("Paracetamol", "Amoxicillin", "Metronidazole", "Antibiotic")),
        SearchResult("Eco Pharmacy", "Kisementi", "2.2 km", "UGX 5,500", "4.9", "12:00 AM", true, tags = listOf("Vitamin C", "Folic Acid", "Supplements", "Gaviscon")),
        SearchResult("Vine Pharmacy", "Lugogo Mall", "3.5 km", "UGX 35,000", "4.6", "10:00 PM", true, tags = listOf("Gaviscon", "Ventolin", "General")),
        SearchResult("Family Health Pharmacy", "Ntinda Road", "4.1 km", "UGX 10,000", "4.3", "9:30 PM", true, tags = listOf("Durex Condoms", "General", "Loratadine"))
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

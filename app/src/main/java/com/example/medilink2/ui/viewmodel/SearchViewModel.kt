package com.example.medilink2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.medilink2.ui.screens.SearchResult

import com.example.medilink2.data.PharmacyRepository

class SearchViewModel : ViewModel() {
    private val repository = PharmacyRepository()
    var searchQuery by mutableStateOf("")
        private set

    val allResults = repository.getAllPharmacies().map { details ->
        SearchResult(
            id = details.id,
            name = details.name,
            location = details.location,
            distance = details.distance,
            price = "UGX 3,000", // Simplified for demo
            rating = details.rating,
            closingTime = details.closingTime,
            inStock = true,
            tags = listOf("General") // Tags could be added to PharmacyDetails later
        )
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun setInitialQuery(query: String?) {
        searchQuery = query ?: ""
    }

    fun getFilteredResults(): List<SearchResult> {
        val query = searchQuery.trim()
        if (query.isEmpty()) return allResults
        
        // Search by pharmacy name or tags
        val pharmacies = allResults.filter { result ->
            result.name.contains(query, ignoreCase = true) ||
            result.tags.any { it.contains(query, ignoreCase = true) }
        }
        
        // Also include pharmacies that stock the searched drug
        val pharmaciesByDrug = repository.getPharmaciesStocking(query).map { details ->
            SearchResult(
                id = details.id,
                name = details.name,
                location = details.location,
                distance = details.distance,
                price = "Varies",
                rating = details.rating,
                closingTime = details.closingTime,
                inStock = true,
                tags = listOf("Stockist")
            )
        }
        
        return (pharmacies + pharmaciesByDrug).distinctBy { it.id }
    }

    fun getMatchingDrugs(): List<com.example.medilink2.ui.components.DrugItem> {
        if (searchQuery.isBlank()) return emptyList()
        return repository.searchDrugs(searchQuery)
    }
}

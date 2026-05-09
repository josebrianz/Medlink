package com.example.medilink2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.ui.components.DrugItem
import com.example.medilink2.ui.screens.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = PharmacyRepository()
    var searchQuery by mutableStateOf("")
        private set

    private val _allResults = MutableStateFlow<List<SearchResult>>(emptyList())
    
    val resultsState: StateFlow<List<SearchResult>> = combine(
        repository.getAllPharmacies(),
        MutableStateFlow(searchQuery) 
    ) { pharmacies, _ ->
        pharmacies.map { details ->
            SearchResult(
                id = details.id,
                name = details.name,
                location = details.location,
                distance = details.distance,
                price = "Varies",
                rating = details.rating,
                closingTime = details.closingTime,
                inStock = details.isOpen,
                tags = listOf("General")
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var allResults by mutableStateOf<List<SearchResult>>(emptyList())

    init {
        viewModelScope.launch {
            repository.getAllPharmacies().collect { pharmacies ->
                allResults = pharmacies.map { details ->
                    SearchResult(
                        id = details.id,
                        name = details.name,
                        location = details.location,
                        distance = details.distance,
                        price = "Varies",
                        rating = details.rating,
                        closingTime = details.closingTime,
                        inStock = details.isOpen,
                        tags = listOf("General")
                    )
                }
            }
        }
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
            val drug = details.inventory.find { it.name.contains(query, ignoreCase = true) }
            SearchResult(
                id = details.id,
                name = details.name,
                location = details.location,
                distance = details.distance,
                price = drug?.price ?: "Varies",
                rating = details.rating,
                closingTime = details.closingTime,
                inStock = drug?.inStock ?: true,
                tags = listOf("Stockist")
            )
        }
        
        return (pharmacies + pharmaciesByDrug).distinctBy { it.id }
    }

    fun getMatchingDrugs(): List<DrugItem> {
        if (searchQuery.isBlank()) return emptyList()
        return repository.searchDrugs(searchQuery)
    }
}

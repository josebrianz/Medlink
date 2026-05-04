package com.example.medilink2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.ui.screens.SearchResult
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = PharmacyRepository()
    
    var searchQuery by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _allResults = mutableStateListOf<SearchResult>()
    val allResults: List<SearchResult> get() = _allResults

    init {
        fetchPharmacies()
    }

    fun fetchPharmacies() {
        viewModelScope.launch {
            isLoading = true
            val results = repository.getAllPharmacies()
            _allResults.clear()
            _allResults.addAll(results)
            isLoading = false
        }
    }

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

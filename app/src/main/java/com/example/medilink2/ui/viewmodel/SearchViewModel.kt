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

package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.ui.theme.*
import com.example.medilink2.ui.viewmodel.SearchViewModel

data class SearchResult(
    val name: String,
    val location: String,
    val distance: String,
    val price: String,
    val rating: String,
    val closingTime: String,
    val inStock: Boolean,
    val stockLevel: String = "Medium",
    val tags: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String? = null,
    onNavigateToHome: () -> Unit = {},
    onNavigateToPharmacy: (String, String?) -> Unit = { _, _ -> },
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    searchViewModel: SearchViewModel = viewModel()
) {
    var searchQuery by rememberSaveable { mutableStateOf(initialQuery ?: "") }
    var selectedDrugName by rememberSaveable { mutableStateOf<String?>(null) }

    // Use live data from the database
    val allResults = searchViewModel.allResults
    val isLoading = searchViewModel.isLoading

    val trimmedQuery = searchQuery.trim()
    val filteredResults = allResults.filter { result ->
        if (selectedDrugName != null) {
            result.tags.any { it.equals(selectedDrugName, ignoreCase = true) }
        } else if (trimmedQuery.isEmpty()) {
            true
        } else {
            result.tags.any { it.contains(trimmedQuery, ignoreCase = true) } ||
            result.name.contains(trimmedQuery, ignoreCase = true)
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Medicines", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = { 
            BottomNavigationBar(
                currentScreen = "Search", 
                onNavigateToHome = onNavigateToHome
            ) 
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        selectedDrugName = null // Reset selection when typing
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for medicines...") },
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { 
                        IconButton(onClick = { keyboardController?.hide() }) {
                            Icon(Icons.Outlined.Search, contentDescription = "Search")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = ""
                                selectedDrugName = null
                                keyboardController?.hide()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                    }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = TealPrimary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (selectedDrugName != null) {
                Text(
                    text = "Pharmacies Stocking \"$selectedDrugName\"",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            } else if (trimmedQuery.isNotEmpty()) {
                Text(
                    text = "Results for \"$trimmedQuery\"",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = "All Nearby Pharmacies",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealPrimary)
                }
            } else if (allResults.isEmpty()) {
                // Helpful empty state for first-time database setup
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CloudOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Database is empty", fontWeight = FontWeight.Bold)
                    Text("Tap below to sync mock data to Firebase", textAlign = TextAlign.Center, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val repo = PharmacyRepository()
                            repo.seedDatabase(listOf(
                                SearchResult("MedPlus Pharmacy", "Kampala Road, Plot 23", "0.8 km", "UGX 3,000", "4.8", "9:00 PM", true, "High", listOf("Paracetamol", "Panadol Extra", "Diclofenac Gel", "Pain Relief", "Gaviscon", "Ziconotide", "ARVs")),
                                SearchResult("City Chemist", "Jinja Road, Near Total", "1.2 km", "UGX 2,500", "4.5", "8:00 PM", true, "Medium", listOf("Paracetamol", "Amoxicillin", "Vitamin C", "Fever", "Augustin"))
                            ))
                            searchViewModel.fetchPharmacies()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Sync with Database")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Show matching drugs ONLY if we haven't selected one yet
                    if (selectedDrugName == null && trimmedQuery.isNotEmpty()) {
                        val matchingDrugs = listOf(
                            "Paracetamol", "Aspirin 81mg", "Insulin Glargine", "Atorvastatin", 
                            "Panadol Extra", "Loratadine", "Salbutamol Inhaler", "Cetirizine", "Amoxicillin", "Ziconotide", "ARVs"
                        ).filter { it.contains(trimmedQuery, ignoreCase = true) }

                        if (matchingDrugs.isNotEmpty()) {
                            item {
                                Text(
                                    "Matching Medicines",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(matchingDrugs) { drug ->
                                SimpleDrugCard(drug) {
                                    selectedDrugName = drug
                                    searchQuery = drug
                                    keyboardController?.hide()
                                }
                            }
                        }
                    }

                    if (filteredResults.isNotEmpty()) {
                        item {
                            Text(
                                if (selectedDrugName != null) "Pharmacies nearby" else "Nearby Pharmacies",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(filteredResults) { result ->
                            SearchResultCard(
                                result = result,
                                isSearching = selectedDrugName != null,
                                onClick = { onNavigateToPharmacy(result.name, selectedDrugName) }
                            )
                        }
                    } else if (trimmedQuery.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 64.dp, horizontal = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = Color.Gray.copy(alpha = 0.3f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No pharmacy has that drug",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "We couldn't find any pharmacy with \"$trimmedQuery\". Try checking the spelling or searching for another medicine.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleDrugCard(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(name, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
    }
}

@Composable
fun SearchResultCard(
    result: SearchResult,
    isSearching: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(if (result.inStock) TealPrimary else Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AddCircle, 
                        contentDescription = null, 
                        tint = if (result.inStock) Color.White else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = result.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSearching) {
                            Spacer(modifier = Modifier.width(8.dp))
                            StockBadge(result.inStock)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Place, 
                            contentDescription = null, 
                            modifier = Modifier.size(14.dp), 
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${result.location} • ${result.distance}",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Text(" ${result.rating}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Text(" ${result.closingTime}", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockBadge(inStock: Boolean) {
    Surface(
        color = if (inStock) InStock else OutOfStock,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (inStock) "In Stock" else "Out of Stock",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = if (inStock) TealPrimary else StatusClosed,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    Medilink2Theme {
        SearchScreen()
    }
}

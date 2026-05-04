package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.ui.components.DrugItem
import com.example.medilink2.ui.components.DrugStockCard
import com.example.medilink2.ui.components.NotificationBadge
import com.example.medilink2.ui.theme.*

data class SearchResult(
    val id: String,
    val name: String,
    val location: String,
    val distance: String,
    val price: String,
    val rating: String,
    val closingTime: String,
    val inStock: Boolean,
    val tags: List<String> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String? = null,
    onNavigateToHome: () -> Unit = {},
    onNavigateToPharmacy: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf(initialQuery ?: "") }

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

    val filteredResults = allResults.filter { result ->
        if (searchQuery.isEmpty()) true
        else result.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
             result.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search", fontWeight = FontWeight.Bold) },
                actions = {
                    NotificationBadge(
                        onClick = onNavigateToNotifications,
                        iconColor = TealPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = { 
            BottomNavigationBar(
                currentScreen = "Search", 
                onNavigateToHome = onNavigateToHome,
            ) 
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for medicines...") },
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = TealPrimary,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )
            }

            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "Results for \"$searchQuery\"",
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                val matchingDrugs = if (searchQuery.isEmpty()) {
                    emptyList()
                } else {
                    listOf(
                        DrugItem(id = "1", name = "Paracetamol", category = "Pain Relief", price = "UGX 3,000", inStock = true, stockLevel = "High"),
                        DrugItem(id = "9", name = "Aspirin 81mg", category = "Heart", price = "UGX 2,500", inStock = true, stockLevel = "High"),
                        DrugItem(id = "8", name = "Insulin Glargine", category = "Diabetes", price = "UGX 45,000", inStock = true, stockLevel = "Medium"),
                        DrugItem(id = "10", name = "Atorvastatin", category = "Heart", price = "UGX 18,000", inStock = true, stockLevel = "Low"),
                        DrugItem(id = "5", name = "Panadol Extra", category = "Pain Relief", price = "UGX 4,500", inStock = true, stockLevel = "Low"),
                        DrugItem(id = "14", name = "Loratadine", category = "General", price = "UGX 3,500", inStock = true, stockLevel = "High"),
                        DrugItem(id = "12", name = "Salbutamol Inhaler", category = "General", price = "UGX 15,000", inStock = true, stockLevel = "High"),
                        DrugItem(id = "8", name = "Cetirizine", category = "Allergy", price = "UGX 4,000", inStock = true, stockLevel = "High"),
                        DrugItem(id = "2", name = "Amoxicillin", category = "Antibiotic", price = "UGX 12,000", inStock = true, stockLevel = "Medium"),
                    ).filter { it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
                }

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
                        DrugStockCard(drug)
                    }
                }

                if (searchQuery.isNotEmpty() && filteredResults.isNotEmpty()) {
                    item {
                        Text(
                            "Pharmacies Stocking This",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (searchQuery.isNotEmpty() && filteredResults.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No pharmacies found for \"$searchQuery\"", color = Color.Gray)
                        }
                    }
                }

                items(filteredResults) { result ->
                    SearchResultCard(
                        result = result,
                        isSearching = searchQuery.isNotEmpty(),
                    ) { onNavigateToPharmacy(result.id) }
                }
            }
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            text = "${result.location} \u2022 ${result.distance}",
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
                        if (isSearching) {
                            Text(result.price, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        } else {
                            Box(modifier = Modifier.width(1.dp))
                        }
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

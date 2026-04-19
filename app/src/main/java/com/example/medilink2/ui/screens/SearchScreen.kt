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
import com.example.medilink2.ui.theme.*

data class SearchResult(
    val name: String,
    val location: String,
    val distance: String,
    val price: String,
    val rating: String,
    val closingTime: String,
    val inStock: Boolean,
    val tags: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String? = null,
    onNavigateToHome: () -> Unit = {},
    onNavigateToPharmacy: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf(initialQuery ?: "Paracetamol") }

    val allResults = listOf(
        SearchResult("MedPlus Pharmacy", "Kampala Road, Plot 23", "0.8 km", "UGX 3,000", "4.8", "9:00 PM", true, listOf("Paracetamol", "Panadol Extra", "Diclofenac Gel", "Pain Relief", "Gaviscon")),
        SearchResult("City Chemist", "Jinja Road, Near Total", "1.2 km", "UGX 2,500", "4.5", "8:00 PM", true, listOf("Paracetamol", "Amoxicillin", "Vitamin C", "Fever", "Augustin")),
        SearchResult("HealthGuard Pharmacy", "Nasser Road, Block B", "1.8 km", "UGX 3,500", "4.2", "10:00 PM", true, listOf("Aspirin 81mg", "Atorvastatin", "Lisinopril", "Heart", "Amlodipine")),
        SearchResult("QuickMeds", "Bombo Road, Wandegeya", "3.1 km", "UGX 2,800", "4.6", "7:00 PM", false, listOf("Insulin Glargine", "Metformin", "Diabetes")),
        SearchResult("Allergy Care", "Wandegeya Market", "2.0 km", "UGX 4,000", "4.4", "6:00 PM", true, listOf("Cetirizine", "Loratadine", "Allergy", "Piriton")),
        SearchResult("General Wellness", "Mulago Hill", "2.5 km", "UGX 1,500", "4.1", "11:00 PM", true, listOf("ORS Sachet", "Salbutamol Inhaler", "Omeprazole", "General", "Folic Acid")),
        SearchResult("First Care Pharmacy", "Kikuubo Lane", "0.5 km", "UGX 2,200", "4.7", "11:00 PM", true, listOf("Paracetamol", "Amoxicillin", "Metronidazole", "Antibiotic")),
        SearchResult("Eco Pharmacy", "Kisementi", "2.2 km", "UGX 5,500", "4.9", "12:00 AM", true, listOf("Vitamin C", "Folic Acid", "Supplements", "Gaviscon")),
        SearchResult("Vine Pharmacy", "Lugogo Mall", "3.5 km", "UGX 35,000", "4.6", "10:00 PM", true, listOf("Gaviscon", "Ventolin", "General")),
        SearchResult("Family Health Pharmacy", "Ntinda Road", "4.1 km", "UGX 10,000", "4.3", "9:30 PM", true, listOf("Durex Condoms", "General", "Loratadine"))
    )

    val filteredResults = allResults.filter { result ->
        if (searchQuery.isEmpty()) true
        else result.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
             result.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
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
                // Find matching drugs across all results to show them directly
                val matchingDrugs = if (searchQuery.isEmpty()) {
                    emptyList()
                } else {
                    listOf(
                        DrugItem("Paracetamol", "Pain Relief", "UGX 3,000", true, "High"),
                        DrugItem("Aspirin 81mg", "Heart", "UGX 2,500", true, "High"),
                        DrugItem("Insulin Glargine", "Diabetes", "UGX 45,000", true, "Medium"),
                        DrugItem("Atorvastatin", "Heart", "UGX 18,000", true, "Low"),
                        DrugItem("Panadol Extra", "Pain Relief", "UGX 4,500", true, "Low"),
                        DrugItem("Loratadine", "General", "UGX 3,500", true, "High"),
                        DrugItem("Salbutamol Inhaler", "General", "UGX 15,000", true, "High"),
                        DrugItem("Cetirizine", "Allergy", "UGX 4,000", true, "High"),
                        DrugItem("Amoxicillin", "Antibiotic", "UGX 12,000", true, "Medium")
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

                if (searchQuery.isNotEmpty()) {
                    item {
                        Text(
                            "Pharmacies Stocking This",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(filteredResults) { result ->
                    SearchResultCard(
                        result = result,
                        isSearching = searchQuery.isNotEmpty(),
                        onClick = onNavigateToPharmacy
                    )
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
                        if (isSearching) {
                            Text(result.price, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        } else {
                            // Empty box to maintain layout alignment when price is hidden
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

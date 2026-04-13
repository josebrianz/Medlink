package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    val inStock: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToHome: () -> Unit = {}
) {
    val results = listOf(
        SearchResult("MedPlus Pharmacy", "Kampala Road, Plot 23", "0.8 km", "UGX 3,000", "4.8", "9:00 PM", true),
        SearchResult("City Chemist", "Jinja Road, Near Total", "1.2 km", "UGX 2,500", "4.5", "8:00 PM", true),
        SearchResult("HealthGuard Pharmacy", "Nasser Road, Block B", "1.8 km", "UGX 3,500", "4.2", "10:00 PM", true),
        SearchResult("QuickMeds", "Bombo Road, Wandegeya", "3.1 km", "UGX 2,800", "4.6", "7:00 PM", false)
    )

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
                    value = "Paracetamol",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(results) { result ->
                    SearchResultCard(result)
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(result: SearchResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        Text(result.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        StockBadge(result.inStock)
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
                        Text(result.price, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.ui.theme.*

data class PharmacyDetails(
    val name: String,
    val location: String,
    val distance: String,
    val rating: String,
    val closingTime: String,
    val inventory: List<DrugItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyDetailScreen(
    onBack: () -> Unit = {}
) {
    // Accessing the inventory and price retrieval through the Repository
    val repository = remember { PharmacyRepository() }
    val pharmacy = remember { repository.getPharmacyDetails("1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pharmacy Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            // Pharmacy Info Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(pharmacy.name, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${pharmacy.location} • ${pharmacy.distance}", color = TextSecondary, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                        Text(" ${pharmacy.rating}", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Text(" Closes at ${pharmacy.closingTime}", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }

            Text(
                "Available Stock & Prices",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(pharmacy.inventory) { drug ->
                    DrugStockCard(drug)
                }
            }
        }
    }
}

data class DrugItem(
    val name: String,
    val category: String,
    val price: String,
    val inStock: Boolean,
    val stockLevel: String
)

@Composable
fun DrugStockCard(drug: DrugItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(drug.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Text(drug.category, color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val stockColor = when(drug.stockLevel) {
                        "High" -> TealPrimary
                        "Medium" -> Color(0xFFFFA000)
                        "Low" -> Color(0xFFD32F2F)
                        else -> Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(stockColor, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stock: ${drug.stockLevel}", color = TextSecondary, fontSize = 12.sp)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(drug.price, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TealPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = if (drug.inStock) InStock else OutOfStock,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (drug.inStock) "In Stock" else "Out of Stock",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = if (drug.inStock) TealDark else StatusClosed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PharmacyDetailPreview() {
    Medilink2Theme {
        PharmacyDetailScreen()
    }
}

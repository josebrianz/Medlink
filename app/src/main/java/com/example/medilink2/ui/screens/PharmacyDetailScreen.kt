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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.data.NotificationLogicManager
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.data.UserManager
import com.example.medilink2.ui.components.DrugItem
import com.example.medilink2.ui.components.DrugStockCard
import com.example.medilink2.ui.theme.*

data class PharmacyDetails(
    val id: String,
    val name: String,
    val location: String,
    val distance: String,
    val rating: String,
    val closingTime: String,
    val inventory: List<DrugItem>,
    val latitude: Double = 0.3476,
    val longitude: Double = 32.5825,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyDetailScreen(
    pharmacyId: String = "1",
    onBack: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToNavigate: () -> Unit = {},
) {
    val repository = remember { PharmacyRepository() }
    val pharmacy = remember(pharmacyId) { repository.getPharmacyDetails(pharmacyId) }
    val userId = UserManager.getUserId()
    val context = LocalContext.current

    LaunchedEffect(userId) {
        userId?.let {
            NotificationLogicManager.checkStockAndNotify(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pharmacy Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    com.example.medilink2.ui.components.NotificationBadge(
                        onClick = onNavigateToNotifications,
                        iconColor = TealPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
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

            Button(
                onClick = onNavigateToNavigate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Directions, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Navigate to Pharmacy")
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
                    DrugStockCard(
                        drug = drug,
                        pharmacyId = pharmacy.id,
                        pharmacyName = pharmacy.name,
                        userId = userId
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

package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.data.UserManager
import com.example.medilink2.ui.theme.TealPrimary
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyDashboardScreen(
    onLogout: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val userId = UserManager.getUserId() ?: return
    val database = FirebaseDatabase.getInstance()
    var pharmacyName by remember { mutableStateOf("") }
    var pharmacyLocation by remember { mutableStateOf("") }
    var isPharmacyRegistered by remember { mutableStateOf(false) }
    var drugs by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var showAddDrugDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch Pharmacy Info and Drugs
    LaunchedEffect(userId) {
        val pharmacyRef = database.getReference("pharmacies").child(userId)
        pharmacyRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                pharmacyName = snapshot.child("name").value as? String ?: ""
                pharmacyLocation = snapshot.child("location").value as? String ?: ""
                isPharmacyRegistered = true
                
                val drugsSnapshot = snapshot.child("drugs")
                val drugsList = mutableListOf<Map<String, Any>>()
                drugsSnapshot.children.forEach { child ->
                    val drug = child.value as? Map<String, Any>
                    if (drug != null) {
                        drugsList.add(drug + ("id" to child.key!!))
                    }
                }
                drugs = drugsList
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pharmacy Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isPharmacyRegistered) {
                FloatingActionButton(
                    onClick = { showAddDrugDialog = true },
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add Drug")
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (!isPharmacyRegistered) {
            RegisterPharmacyView(
                userId = userId,
                onRegistered = { name, loc ->
                    pharmacyName = name
                    pharmacyLocation = loc
                    isPharmacyRegistered = true
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Welcome, $pharmacyName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = pharmacyLocation,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Inventory Management",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(drugs) { drug ->
                        DrugInventoryCard(
                            drug = drug,
                            onUpdateStock = { drugId, newStock ->
                                val inStock = (newStock.toIntOrNull() ?: 0) > 0
                                val updates = mapOf(
                                    "stockLevel" to newStock,
                                    "inStock" to inStock
                                )
                                database.getReference("pharmacies")
                                    .child(userId)
                                    .child("drugs")
                                    .child(drugId)
                                    .updateChildren(updates)
                                    
                                drugs = drugs.map { 
                                    if (it["id"] == drugId) it + updates else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDrugDialog) {
        AddDrugDialog(
            onDismiss = { showAddDrugDialog = false },
            onConfirm = { name, category, price, stock ->
                val drugId = database.getReference("pharmacies").child(userId).child("drugs").push().key ?: ""
                val drugMap = mapOf(
                    "name" to name,
                    "category" to category,
                    "price" to price,
                    "stockLevel" to stock,
                    "inStock" to (stock.toIntOrNull() ?: 0 > 0)
                )
                database.getReference("pharmacies")
                    .child(userId)
                    .child("drugs")
                    .child(drugId)
                    .setValue(drugMap)
                
                drugs = drugs + (drugMap + ("id" to drugId))
                showAddDrugDialog = false
            }
        )
    }
}

@Composable
fun RegisterPharmacyView(
    userId: String,
    onRegistered: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register Your Pharmacy", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Provide details to start managing your inventory", color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Pharmacy Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Pharmacy Location") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if (name.isNotBlank() && location.isNotBlank()) {
                    isRegistering = true
                    val pharmacyMap = mapOf(
                        "name" to name,
                        "location" to location,
                        "rating" to "0.0",
                        "closingTime" to "9:00 PM"
                    )
                    FirebaseDatabase.getInstance().getReference("pharmacies")
                        .child(userId)
                        .setValue(pharmacyMap)
                        .addOnSuccessListener {
                            onRegistered(name, location)
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            enabled = !isRegistering
        ) {
            if (isRegistering) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Register Pharmacy")
            }
        }
    }
}

@Composable
fun DrugInventoryCard(
    drug: Map<String, Any>,
    onUpdateStock: (String, String) -> Unit
) {
    val drugId = drug["id"] as String
    val name = drug["name"] as String
    val category = drug["category"] as String
    val price = drug["price"] as String
    val stockLevel = drug["stockLevel"] as String

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(price, fontWeight = FontWeight.Bold, color = TealPrimary)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("Stock: $stockLevel", fontWeight = FontWeight.SemiBold)
                Row {
                    IconButton(onClick = { 
                        val current = stockLevel.toIntOrNull() ?: 0
                        if (current > 0) onUpdateStock(drugId, (current - 1).toString())
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    IconButton(onClick = { 
                        val current = stockLevel.toIntOrNull() ?: 0
                        onUpdateStock(drugId, (current + 1).toString())
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }
        }
    }
}

@Composable
fun AddDrugDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Drug") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Drug Name") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (e.g. UGX 5,000)") })
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Initial Stock") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, category, price, stock) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

package com.example.medilink2.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.data.NotificationLogicManager
import com.example.medilink2.ui.theme.*
import kotlinx.coroutines.launch

data class DrugItem(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val inStock: Boolean,
    val stockLevel: String,
)

@Composable
fun DrugStockCard(
    drug: DrugItem,
    pharmacyId: String? = null,
    pharmacyName: String? = null,
    userId: String? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val isSubscribed by if (userId != null && (drug.id.isNotEmpty()) && (pharmacyId != null)) {
        NotificationLogicManager.isSubscribed(userId, drug.id, pharmacyId).collectAsState(initial = false)
    } else {
        remember { mutableStateOf(value = false) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(drug.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text(drug.category, color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val stockColor = when (drug.stockLevel) {
                            "High" -> TealPrimary
                            "Medium" -> Color(0xFFFFA000)
                            "Low" -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(stockColor, RoundedCornerShape(4.dp)),
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

            if (!drug.inStock && userId != null && pharmacyId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        scope.launch {
                            if (isSubscribed) {
                                NotificationLogicManager.removeNotificationRequest(userId, drug.id, pharmacyId)
                                Toast.makeText(context, "Removed notification request", Toast.LENGTH_SHORT).show()
                            } else {
                                NotificationLogicManager.saveNotificationRequest(
                                    userId, 
                                    drug, 
                                    pharmacyId, 
                                    pharmacyName ?: "Unknown Pharmacy"
                                )
                                Toast.makeText(context, "You'll be notified when available", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) Color.LightGray else TealPrimary,
                        contentColor = if (isSubscribed) Color.DarkGray else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSubscribed) Icons.Default.CheckCircle else Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSubscribed) "You'll be notified" else "Notify me when available",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

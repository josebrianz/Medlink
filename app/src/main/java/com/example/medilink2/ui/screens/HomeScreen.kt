package com.example.medilink2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.ui.theme.*

data class Category(val name: String, val icon: ImageVector, val color: Color)
data class Pharmacy(val name: String, val location: String, val distance: String, val isOpen: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit = {},
    onNavigateToPharmacy: () -> Unit = {}
) {
    val categories = listOf(
        Category("Pain Relief", Icons.Default.AddCircle, CategoryPainRelief),
        Category("Fever", Icons.Default.Face, CategoryFever),
        Category("Heart", Icons.Default.Favorite, CategoryHeart),
        Category("General", Icons.Default.Star, CategoryGeneral)
    )

    val recentSearches = listOf("Paracetamol", "Amoxicillin 500mg", "Ibuprofen", "Metformin")

    val pharmacies = listOf(
        Pharmacy("MedPlus Pharmacy", "Kampala Road", "0.8 km", true),
        Pharmacy("City Chemist", "Jinja Road", "1.2 km", true),
        Pharmacy("LifeCare Pharmacy", "Entebbe Road", "2.5 km", false)
    )

    Scaffold(
        bottomBar = { 
            BottomNavigationBar(
                currentScreen = "Home", 
                onNavigateToSearch = onNavigateToSearch
            ) 
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            item { HeaderSection() }
            
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        categories.forEach { category ->
                            CategoryItem(category)
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent Searches", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp
                    ) {
                        recentSearches.forEach { search ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text(search) },
                                shape = RoundedCornerShape(20.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nearby Pharmacies", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("See all", color = TealPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            items(pharmacies) { pharmacy ->
                PharmacyCard(
                    pharmacy = pharmacy,
                    onClick = onNavigateToPharmacy
                )
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = TealPrimary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good morning 👋", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                    Text("John Doe", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Row {
                    IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                        Icon(Icons.Outlined.Place, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search medicines, drugs...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(category.color, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(category.icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.name, fontSize = 12.sp, color = TextPrimary)
    }
}

@Composable
fun PharmacyCard(
    pharmacy: Pharmacy,
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(TealPrimary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(pharmacy.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(pharmacy.location, color = TextSecondary, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (pharmacy.isOpen) "Open" else "Closed",
                    color = if (pharmacy.isOpen) StatusOpen else StatusClosed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(pharmacy.distance, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = TealPrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = currentScreen == "Home",
            onClick = onNavigateToHome
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search") },
            selected = currentScreen == "Search",
            onClick = onNavigateToSearch
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            label = { Text("Navigate") },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = false,
            onClick = {}
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(mainAxisSpacing),
        verticalArrangement = Arrangement.spacedBy(crossAxisSpacing),
        content = { content() }
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Medilink2Theme {
        HomeScreen()
    }
}

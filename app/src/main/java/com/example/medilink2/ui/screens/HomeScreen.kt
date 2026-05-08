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
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.ui.theme.*
import com.example.medilink2.ui.components.NotificationBadge

data class Category(val name: String, val icon: ImageVector, val color: Color)
data class Pharmacy(val id: String, val name: String, val location: String, val distance: String, val isOpen: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: (String?) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNavigate: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onPharmacyClick: (String) -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val user = auth.currentUser
    var fullName by rememberSaveable { mutableStateOf(user?.displayName ?: "User") }

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            val database = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users").child(uid)
            database.child("fullName").get().addOnSuccessListener { snapshot ->
                snapshot.getValue(String::class.java)?.let {
                    fullName = it
                }
            }
        }
    }

    val categories = listOf(
        Category("Pain Relief", Icons.Default.AddCircle, CategoryPainRelief),
        Category("Fever", Icons.Default.Face, CategoryFever),
        Category("Heart", Icons.Default.Favorite, CategoryHeart),
        Category("General", Icons.Default.Star, CategoryGeneral)
    )

    val recentSearches = listOf("Paracetamol", "Amoxicillin 500mg", "Ibuprofen", "Metformin")

    val pharmacies = listOf(
        Pharmacy("1", "MedPlus Pharmacy", "Kampala Road", "0.8 km", true),
        Pharmacy("2", "City Chemist", "Jinja Road", "1.2 km", true),
        Pharmacy("3", "LifeCare Pharmacy", "Entebbe Road", "2.5 km", false)
    )

    Scaffold(
        bottomBar = { 
            BottomNavigationBar(
                currentScreen = "Home", 
                onNavigateToHome = { /* Already here */ },
                onNavigateToSearch = { onNavigateToSearch(null) },
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToNavigate = onNavigateToNavigate
            ) 
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item { 
                HeaderSection(
                    userName = fullName,
                    onSearchClick = { onNavigateToSearch(null) },
                    onNotificationClick = onNotificationClick,
                    onPlaceClick = onNavigateToNavigate,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode
                ) 
            }
            
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Categories", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        categories.forEach { category ->
                            CategoryItem(category, onClick = { onNavigateToSearch(category.name) })
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Recent Searches", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentSearches.chunked(2).forEach { chunk ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                chunk.forEach { search ->
                                    SuggestionChip(
                                        onClick = { onNavigateToSearch(search) },
                                        label = { Text(search) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            labelColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
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
                    Text(
                        "Nearby Pharmacies", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "See all", 
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToSearch(null) }
                    )
                }
            }

            items(pharmacies) { pharmacy ->
                PharmacyCard(pharmacy, onClick = { onPharmacyClick(pharmacy.id) })
            }
        }
    }
}

@Composable
fun HeaderSection(
    userName: String = "John Doe",
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onPlaceClick: () -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
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
                    Text("Hello 👋", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                    Text(userName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Row {
                    IconButton(onClick = onToggleDarkMode, modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                        Icon(
                            if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null, 
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onPlaceClick, modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                        Icon(Icons.Outlined.Place, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    NotificationBadge(onClick = onNotificationClick)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick() }
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Search medicines, drugs...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false, // Box handles the click
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(category.color, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(category.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun PharmacyCard(pharmacy: Pharmacy, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    pharmacy.name, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    pharmacy.location, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    fontSize = 14.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (pharmacy.isOpen) "Open" else "Closed",
                    color = if (pharmacy.isOpen) StatusOpen else StatusClosed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    pharmacy.distance, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToNavigate: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = currentScreen == "Home",
            onClick = onNavigateToHome,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search") },
            selected = currentScreen == "Search",
            onClick = onNavigateToSearch,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            label = { Text("Navigate") },
            selected = currentScreen == "Navigate",
            onClick = onNavigateToNavigate,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = currentScreen == "Profile",
            onClick = onNavigateToProfile,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Medilink2Theme {
        HomeScreen()
    }
}

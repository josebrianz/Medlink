package com.example.medilink2.ui.screens

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.ui.theme.Background
import com.example.medilink2.ui.theme.TealPrimary
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    destinationPharmacyId: String? = null,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: (String?) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNavigate: () -> Unit = {},
    onNavigateToPharmacy: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val repository = remember { PharmacyRepository() }
    
    Configuration.getInstance().userAgentValue = context.packageName

    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var locationSearchQuery by remember { mutableStateOf("") }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = "Navigate",
                onNavigateToHome = onNavigateToHome,
                onNavigateToSearch = { onNavigateToSearch(null) },
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToNavigate = onNavigateToNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                        try {
                            @SuppressLint("MissingPermission")
                            val lastLocationTask = fusedLocationClient.lastLocation
                            lastLocationTask.addOnSuccessListener { location: Location? ->
                                location?.let {
                                    val userPoint = GeoPoint(it.latitude, it.longitude)
                                    mapView.controller.animateTo(userPoint)
                                    mapView.controller.setZoom(15.0)
                                }
                            }
                        } catch (e: SecurityException) {
                        }
                    }
                },
                containerColor = TealPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            AndroidView(
                factory = { ctx ->
                    Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(0.3476, 32.5825))

                        if (hasLocationPermission) {
                            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                            locationOverlay.enableMyLocation()
                            locationOverlay.enableFollowLocation()
                            locationOverlay.runOnFirstFix {
                                val location = locationOverlay.myLocation
                                if (location != null) {
                                    userLocation = location
                                    if (destinationPharmacyId != null) {
                                        val pharmacy = repository.getPharmacyDetails(destinationPharmacyId)
                                        val destPoint = GeoPoint(pharmacy.latitude, pharmacy.longitude)
                                        
                                        // Draw a simple polyline for routing
                                        val line = Polyline()
                                        line.setPoints(listOf(location, destPoint))
                                        line.color = 0xFF008080.toInt() // TealPrimary color
                                        line.width = 5.0f
                                        overlays.add(line)
                                        
                                        post {
                                            controller.animateTo(destPoint)
                                            controller.setZoom(16.0)
                                        }
                                    }
                                }
                            }
                            overlays.add(locationOverlay)
                        }

                        addPharmacyMarker(this, "MedPlus Pharmacy (Kampala Road)", GeoPoint(0.3136, 32.5811)) { onNavigateToPharmacy("1") }
                        addPharmacyMarker(this, "City Chemist (Jinja Road)", GeoPoint(0.3162, 32.5855)) { onNavigateToPharmacy("2") }
                        addPharmacyMarker(this, "HealthGuard Pharmacy (Nasser Rd)", GeoPoint(0.3120, 32.5880)) { onNavigateToPharmacy("3") }
                        addPharmacyMarker(this, "First Care Pharmacy (Kikuubo)", GeoPoint(0.3140, 32.5780)) { onNavigateToPharmacy("4") }
                        addPharmacyMarker(this, "Vine Pharmacy (Lugogo Mall)", GeoPoint(0.3250, 32.6020)) { onNavigateToPharmacy("5") }
                        addPharmacyMarker(this, "Eco Pharmacy (Kisementi)", GeoPoint(0.3350, 32.5950)) { onNavigateToPharmacy("6") }
                        addPharmacyMarker(this, "Family Health Pharmacy (Ntinda)", GeoPoint(0.3540, 32.6110)) { onNavigateToPharmacy("7") }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = locationSearchQuery,
                    onValueChange = { 
                        locationSearchQuery = it
                        if (it.contains("Kampala", ignoreCase = true)) {
                            mapView.controller.animateTo(GeoPoint(0.3476, 32.5825))
                        } else if (it.contains("Jinja", ignoreCase = true)) {
                            mapView.controller.animateTo(GeoPoint(0.4479, 33.2032))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search location or pharmacy...") },
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TealPrimary) },
                    trailingIcon = {
                        if (locationSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { locationSearchQuery = "" }) {
                                Icon(Icons.Default.MyLocation, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = TealPrimary,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }
    }
}

private fun addPharmacyMarker(mapView: MapView, title: String, position: GeoPoint, onClick: () -> Unit) {
    val marker = Marker(mapView)
    marker.position = position
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    marker.title = title
    marker.setOnMarkerClickListener { m, _ ->
        m.showInfoWindow()
        onClick()
        true
    }
    mapView.overlays.add(marker)
}

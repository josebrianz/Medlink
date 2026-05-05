package com.example.medilink2.ui.screens

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.content.Intent
import android.provider.Settings
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
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
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
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
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

    val locationOverlay = remember { MyLocationNewOverlay(GpsMyLocationProvider(context), mapView) }
    var showGpsDisabledDialog by remember { mutableStateOf(false) }

    // Check GPS status
    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledDialog = true
        }
    }

    if (showGpsDisabledDialog) {
        AlertDialog(
            onDismissRequest = { showGpsDisabledDialog = false },
            title = { Text("GPS Disabled") },
            text = { Text("GPS is required for accurate navigation and showing your location on the map. Would you like to enable it?") },
            confirmButton = {
                TextButton(onClick = {
                    showGpsDisabledDialog = false
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text("Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGpsDisabledDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Polyline for the route
    val routePolyline = remember { 
        Polyline().apply {
            color = 0xFF008080.toInt() // TealPrimary
            width = 12.0f
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (hasLocationPermission) {
                locationOverlay.enableMyLocation()
            }
        }
    )

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            locationOverlay.enableMyLocation()
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1000)
                .build()

            callbackFlow {
                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { trySend(it) }
                    }
                }
                try {
                    fusedLocationClient.requestLocationUpdates(locationRequest, callback, context.mainLooper)
                } catch (e: SecurityException) {}
                awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
            }.collectLatest { location ->
                val newPoint = GeoPoint(location.latitude, location.longitude)
                userLocation = newPoint
                
                if (destinationPharmacyId != null) {
                    val pharmacy = repository.getPharmacyDetails(destinationPharmacyId)
                    val destPoint = GeoPoint(pharmacy.latitude, pharmacy.longitude)
                    routePolyline.setPoints(listOf(newPoint, destPoint))
                    
                    if (!mapView.overlays.contains(routePolyline)) {
                        mapView.overlays.add(routePolyline)
                    }
                    mapView.invalidate()
                }
            }
        } else {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            locationOverlay.disableMyLocation()
            locationOverlay.disableFollowLocation()
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
                    locationOverlay.enableFollowLocation()
                    userLocation?.let {
                        mapView.controller.animateTo(it)
                        mapView.controller.setZoom(17.5)
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
                        controller.setZoom(15.0)
                        
                        if (!overlays.contains(locationOverlay)) {
                            overlays.add(locationOverlay)
                        }
                        
                        if (hasLocationPermission) {
                            locationOverlay.enableMyLocation()
                            locationOverlay.enableFollowLocation()
                        }

                        // Dynamically add all pharmacies from the repository
                        repository.getAllPharmacies().forEach { pharmacy ->
                            addPharmacyMarker(
                                this,
                                "${pharmacy.name} (${pharmacy.location})",
                                GeoPoint(pharmacy.latitude, pharmacy.longitude)
                            ) { onNavigateToPharmacy(pharmacy.id) }
                        }
                        
                        if (destinationPharmacyId != null) {
                            val pharmacy = repository.getPharmacyDetails(destinationPharmacyId)
                            val destPoint = GeoPoint(pharmacy.latitude, pharmacy.longitude)
                            
                            // Add a special marker for the destination
                            val destMarker = Marker(this)
                            destMarker.position = destPoint
                            destMarker.title = "Target: ${pharmacy.name}"
                            destMarker.icon = ContextCompat.getDrawable(ctx, org.osmdroid.library.R.drawable.marker_default)
                            destMarker.icon?.setTint(0xFF008080.toInt()) // Teal
                            overlays.add(destMarker)
                            
                            if (!overlays.contains(routePolyline)) {
                                overlays.add(routePolyline)
                            }
                            
                            controller.setCenter(destPoint)
                            controller.setZoom(17.0)
                        } else {
                            controller.setCenter(GeoPoint(0.3476, 32.5825))
                        }
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

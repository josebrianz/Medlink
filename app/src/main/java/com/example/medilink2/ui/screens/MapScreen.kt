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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.medilink2.data.PharmacyRepository
import com.example.medilink2.ui.components.NotificationBadge
import com.example.medilink2.ui.theme.Background
import com.example.medilink2.ui.theme.TealPrimary
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    destinationPharmacyId: String? = null,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: (String?) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNavigate: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavigateToPharmacy: (String) -> Unit = {},
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val repository = remember { PharmacyRepository() }
    val pharmacies by repository.getAllPharmacies().collectAsState(initial = emptyList())
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    Configuration.getInstance().userAgentValue = context.packageName

    var locationSearchQuery by rememberSaveable { mutableStateOf("") }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var hasLocationPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationOverlay = remember { MyLocationNewOverlay(GpsMyLocationProvider(context), mapView) }
    var showGpsDisabledDialog by remember { mutableStateOf(false) }
    var isRouting by remember { mutableStateOf(false) }
    var lastRoutedDestinationId by remember { mutableStateOf<String?>(null) }

    val targetMarker = remember {
        Marker(mapView).apply {
            icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)
            icon?.setTint(0xFF008080.toInt()) // Teal
        }
    }

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
            outlinePaint.color = 0xFF008080.toInt() // TealPrimary
            outlinePaint.strokeWidth = 12.0f
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
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
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
                userLocation = GeoPoint(location.latitude, location.longitude)
            }
        } else {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    // Dedicated LaunchedEffect for Routing
    LaunchedEffect(userLocation, destinationPharmacyId) {
        val start = userLocation
        if (destinationPharmacyId != null) {
            val pharmacy = repository.getPharmacyDetails(destinationPharmacyId)
            if (pharmacy != null) {
                val destPoint = GeoPoint(pharmacy.latitude, pharmacy.longitude)

                targetMarker.position = destPoint
                targetMarker.title = "Target: ${pharmacy.name}"
                if (!mapView.overlays.contains(targetMarker)) {
                    mapView.overlays.add(targetMarker)
                }

                if (start != null) {
                    isRouting = true
                    val routePoints = fetchRoute(start, destPoint, context)
                    routePolyline.setPoints(routePoints)

                    if (!mapView.overlays.contains(routePolyline)) {
                        mapView.overlays.add(routePolyline)
                    }

                    // Zoom to fit the route once per new destination
                    if (destinationPharmacyId != lastRoutedDestinationId) {
                        if (routePoints.size > 2) { // More than just start/end fallback
                            try {
                                val boundingBox = BoundingBox.fromGeoPoints(routePoints)
                                mapView.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true)
                            } catch (e: Exception) {
                                mapView.controller.animateTo(destPoint)
                            }
                        } else {
                            mapView.controller.animateTo(destPoint)
                        }
                        lastRoutedDestinationId = destinationPharmacyId
                    }
                    isRouting = false
                }
            }
            mapView.invalidate()
        } else {
            lastRoutedDestinationId = null
            routePolyline.setPoints(emptyList())
            mapView.overlays.remove(routePolyline)
            mapView.overlays.remove(targetMarker)
            mapView.invalidate()
        }
    }

    val pharmacyMarkers = remember { mutableMapOf<String, Marker>() }

    // Update markers when pharmacies list changes
    LaunchedEffect(pharmacies) {
        // Remove markers for pharmacies no longer in the list
        val currentIds = pharmacies.map { it.id }.toSet()
        pharmacyMarkers.keys.retainAll { id ->
            if (!currentIds.contains(id)) {
                mapView.overlays.remove(pharmacyMarkers[id])
                true
            } else false
        }

        // Add or update markers for pharmacies in the list
        pharmacies.forEach { pharmacy ->
            val marker = pharmacyMarkers.getOrPut(pharmacy.id) {
                Marker(mapView).apply {
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { m, _ ->
                        m.showInfoWindow()
                        onNavigateToPharmacy(pharmacy.id)
                        true
                    }
                    mapView.overlays.add(this)
                }
            }
            marker.position = GeoPoint(pharmacy.latitude, pharmacy.longitude)
            marker.title = "${pharmacy.name} (${pharmacy.location})"
        }
        mapView.invalidate()
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
                    if (hasLocationPermission) {
                        locationOverlay.enableFollowLocation()
                        userLocation?.let {
                            mapView.controller.animateTo(it)
                            mapView.controller.setZoom(17.5)
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
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
                    }
                },
                update = { _ ->
                    // Markers and polylines are managed in LaunchedEffects
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isRouting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealPrimary)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
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
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search location...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        shape = RoundedCornerShape(28.dp),
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            if (locationSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { locationSearchQuery = "" }) {
                                    Icon(Icons.Default.MyLocation, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NotificationBadge(
                        onClick = onNotificationClick,
                        iconColor = if (isDarkMode) Color.White else TealPrimary
                    )
                }
                
                if (destinationPharmacyId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onNavigateToNavigate() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Stop Navigation", color = Color.White)
                    }
                }
            }
        }
    }
}

private suspend fun fetchRoute(start: GeoPoint, end: GeoPoint, context: Context): List<GeoPoint> {
    return withContext(Dispatchers.IO) {
        val result = mutableListOf<GeoPoint>()
        var connection: HttpURLConnection? = null
        try {
            val urlString = "https://router.project-osrm.org/route/v1/driving/" +
                    "${start.longitude},${start.latitude};${end.longitude},${end.latitude}" +
                    "?overview=full&geometries=geojson"
            
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "MedilinkApp/1.0 (" + context.packageName + ")")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.optString("code") == "Ok") {
                    val routes = jsonResponse.optJSONArray("routes")
                    if (routes != null && routes.length() > 0) {
                        val geometry = routes.getJSONObject(0).getJSONObject("geometry")
                        val coordinates = geometry.getJSONArray("coordinates")
                        for (i in 0 until coordinates.length()) {
                            val coord = coordinates.getJSONArray(i)
                            result.add(GeoPoint(coord.getDouble(1), coord.getDouble(0)))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MapScreen", "Exception during route fetch", e)
        } finally {
            connection?.disconnect()
        }
        
        if (result.isEmpty()) {
            listOf(start, end)
        } else {
            result
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

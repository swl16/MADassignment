package com.example.assignment.nearby
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyScreen(navController: NavController,
                 onNavigateBack: () -> Unit = {}) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = "MyHealthCareApp-Assignment/1.0 (student@assignment.local)"

        // 2. Load SharedPreferences configuration
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var facilities by remember { mutableStateOf<List<Facility>>(emptyList()) }
    var selectedFacility by remember { mutableStateOf<Facility?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var osmMapView by remember { mutableStateOf<MapView?>(null) }

//    val defaultLocation = LatLng(3.2152, 101.7289) // Default fallback
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
//    }

    fun loadPlaces(lat: Double, lng: Double) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val osmQuery = """
                    [out:json][timeout:40];
                    (
                      node["amenity"="hospital"](around:3000,$lat,$lng);
                      node["amenity"="clinic"](around:3000,$lat,$lng);
                      node["amenity"="doctors"](around:3000,$lat,$lng);
                      node["amenity"="pharmacy"](around:3000,$lat,$lng);
                    );
                    out body;
                """.trimIndent()

                val response = RetrofitClient.api.getNearbyFacilities(osmQuery)
                val labels = ('A'..'Z').map { it.toString() }

                val mappedList = response.elements
                    ?.filter { it.lat != null && it.lon != null && !it.tags?.name.isNullOrBlank() }
                    ?.mapIndexed { index, item ->
                        val placeLat = item.lat!!
                        val placeLng = item.lon!!

                        // Calculate distance from user GPS
                        val distArr = FloatArray(1)
                        Location.distanceBetween(lat, lng, placeLat, placeLng, distArr)
                        val distKm = String.format("%.1f km", distArr[0] / 1000f)

                        val categoryType = when (item.tags?.amenity) {
                            "hospital" -> "Hospital"
                            "clinic" -> "Clinic"
                            "pharmacy" -> "Pharmacy"
                            else -> "Medical Care"
                        }

                        Facility(
                            id = item.id.toString(),
                            label = labels.getOrElse(index) { "•" },
                            name = item.tags?.name ?: "Medical Center",
                            type = categoryType,
                            distance = distKm,
                            openingHours = item.tags?.openingHours ?: "Open daily",
                            lat = placeLat,
                            lon = placeLng
                        )
                    } ?: emptyList()

                facilities = mappedList
                selectedFacility = mappedList.firstOrNull()
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Function to acquire GPS
    @SuppressLint("MissingPermission")
    fun fetchGPS() {
        isLoading = true
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    val currentPoint = GeoPoint(loc.latitude, loc.longitude)
                    userLocation = currentPoint
                    osmMapView?.controller?.apply {
                        setZoom(15.0)
                        animateTo(currentPoint)
                    }
                    loadPlaces(loc.latitude, loc.longitude)
                } else {
                    isLoading = false
                    errorMessage = "Turn on device GPS location."
                }
            }
            .addOnFailureListener {
                isLoading = false
                errorMessage = "Location error: ${it.localizedMessage}"
            }
    }

    // Runtime Permission Request
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true || perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            fetchGPS()
        } else {
            errorMessage = "Location permission is required."
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine) {
            fetchGPS()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    val filteredFacilities = facilities.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.type.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1E293B)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nearby Care",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }

        // --- Map + Search ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val cleanTileSource = XYTileSource(
                        "OpenTopoMap",
                        0,
                        17,
                        256,
                        ".png",
                        arrayOf(
                            "https://a.tile.opentopomap.org/",
                            "https://b.tile.opentopomap.org/",
                            "https://c.tile.opentopomap.org/"
                        )
                    )

                    MapView(ctx).apply {
                        setTileSource(cleanTileSource)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(userLocation ?: GeoPoint(3.2152, 101.7289))
                        osmMapView = this
                    }

//                    MapView(ctx).apply {
//                        setTileSource(TileSourceFactory.MAPNIK)
//                        setMultiTouchControls(true)
//                        controller.setZoom(15.0)
//                        controller.setCenter(GeoPoint(3.2152, 101.7289))
//                        osmMapView = this
//                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    filteredFacilities.forEach { facility ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(facility.lat, facility.lon)
                            title = "${facility.label}. ${facility.name}"
                            snippet = "${facility.type} • ${facility.distance}"
                            setOnMarkerClickListener { _, _ ->
                                selectedFacility = facility
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                }
            )

            // Floating Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search clinic or hospital", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF3B67E9)
                )
            }
        }

        // --- Bottom Sheet ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby facilities",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "${filteredFacilities.size} found",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                errorMessage?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 240.dp)
                ) {
                    items(filteredFacilities) { facility ->
                        val isSelected = selectedFacility?.id == facility.id

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFF1F5FD) else Color(0xFFF8FAFC)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) Color(0xFF3B67E9) else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedFacility = facility
                                    val targetPoint = GeoPoint(facility.lat, facility.lon)
                                    osmMapView?.controller?.apply {
                                        setZoom(16.0)
                                        animateTo(targetPoint)
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "${facility.label}. ${facility.name}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${facility.type} • ${facility.distance}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = facility.openingHours,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (facility.openingHours == "Open now") Color(0xFF10B981) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedFacility?.let { target ->
                            val gmmIntentUri = Uri.parse("google.navigation:q=${target.lat},${target.lon}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${target.lat},${target.lon}")
                                context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                            }
                        }
                    },
                    enabled = selectedFacility != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B67E9))
                ) {
                    Text(
                        text = "Get directions to selected facility",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NearbyScreenPreview() {
    // Provide an empty list or mock data for preview
    NearbyScreen(navController = rememberNavController())

}
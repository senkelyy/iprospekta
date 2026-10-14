package com.andersen.iprospekta.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.andersen.iprospekta.data.FieldPoint
import com.andersen.iprospekta.location.LocationController
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

private val TILE_SOURCES = listOf(
    TileSourceFactory.MAPNIK,          // Plan OSM
    TileSourceFactory.OpenTopo         // Topographique
)
private val TILE_NAMES = listOf("OSM", "Topo")

@Composable
fun MapScreen(
    points: List<FieldPoint>,
    onAddPoint: (FieldPoint) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val locationController = remember { LocationController(context) }
    var tileIndex by remember { mutableIntStateOf(0) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TILE_SOURCES[0])
            setMultiTouchControls(true)
            controller.setZoom(6.0)
            // Centre par defaut : Madagascar (Antananarivo)
            controller.setCenter(GeoPoint(-18.8792, 47.5079))
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* le resultat est relu via LocationController.hasPermission */ }

    LaunchedEffect(Unit) {
        if (!locationController.hasPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Cycle de vie osmdroid
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { mv ->
                mv.setTileSource(TILE_SOURCES[tileIndex])
                redrawOverlays(mv, points) { geo ->
                    val p = FieldPoint(
                        id = System.currentTimeMillis(),
                        latitude = geo.latitude,
                        longitude = geo.longitude,
                        altitude = 0.0,
                        accuracy = 0f,
                        name = "Point ${points.size + 1}"
                    )
                    onAddPoint(p)
                    Toast.makeText(context, "Point ajouté (appui long)", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Changer de fond (OSM / Topo)
        SmallFloatingActionButton(
            onClick = { tileIndex = (tileIndex + 1) % TILE_SOURCES.size },
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
        ) { Icon(Icons.Filled.Layers, contentDescription = "Fond ${TILE_NAMES[tileIndex]}") }

        // Centrer sur ma position
        SmallFloatingActionButton(
            onClick = {
                val loc = if (locationController.hasPermission(context)) locationController.lastKnown() else null
                if (loc != null) {
                    mapView.controller.animateTo(GeoPoint(loc.latitude, loc.longitude))
                    mapView.controller.setZoom(16.0)
                } else {
                    Toast.makeText(context, "Position GPS indisponible", Toast.LENGTH_SHORT).show()
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) { Icon(Icons.Filled.MyLocation, contentDescription = "Ma position") }

        // Ajouter un point a la position GPS courante
        FloatingActionButton(
            onClick = {
                val loc = if (locationController.hasPermission(context)) locationController.lastKnown() else null
                if (loc != null) {
                    onAddPoint(
                        FieldPoint(
                            id = System.currentTimeMillis(),
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            altitude = loc.altitude,
                            accuracy = loc.accuracy,
                            name = "Point ${points.size + 1}"
                        )
                    )
                    Toast.makeText(context, "Point GPS enregistré", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Position GPS indisponible", Toast.LENGTH_SHORT).show()
                }
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Filled.Add, contentDescription = "Ajouter un point GPS") }
    }
}

/** Reconstruit les overlays : evenements (appui long) + marqueurs des points. */
private fun redrawOverlays(
    mv: MapView,
    points: List<FieldPoint>,
    onLongPress: (GeoPoint) -> Unit
) {
    mv.overlays.clear()

    val receiver = object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
        override fun longPressHelper(p: GeoPoint?): Boolean {
            if (p != null) { onLongPress(p); return true }
            return false
        }
    }
    mv.overlays.add(MapEventsOverlay(receiver))

    points.forEach { fp ->
        val marker = Marker(mv)
        marker.position = GeoPoint(fp.latitude, fp.longitude)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = fp.name
        marker.snippet = buildString {
            append("%.5f, %.5f".format(fp.latitude, fp.longitude))
            if (fp.lithologie.isNotBlank()) append("\n${fp.lithologie}")
            if (fp.pendage != null && fp.direction != null) append("\n${fp.direction}°/${fp.pendage}°")
        }
        mv.overlays.add(marker)
    }
    mv.invalidate()
}

package com.example.regresoacasamaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(true) {
        val location = fusedClient.lastLocation.await()
        viewModel.currentLocation = LatLng(location.latitude, location.longitude)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    viewModel.currentLocation ?: LatLng(0.0, 0.0),
                    15f
                )
            }
        ) {
            viewModel.currentLocation?.let {
                Marker(position = it, title = "Ubicación actual")
            }
            viewModel.homeLocation?.let {
                Marker(position = it, title = "Casa")
            }
            if (viewModel.routePoints.isNotEmpty()) {
                Polyline(points = viewModel.routePoints)
            }
        }

        Column(Modifier.align(Alignment.TopCenter).padding(16.dp)) {
            Button(onClick = { viewModel.getRoute() }) {
                Text("Trazar ruta a casa")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                // Para guardar la ubicación actual como "Casa"
                viewModel.currentLocation?.let {
                    viewModel.saveHomeLocation(it)
                }
            }) {
                Text("Guardar esta ubicación como casa")
            }
        }
    }
}

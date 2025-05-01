package com.example.regresoacasamaps

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val db: AppDatabase,
    private val api: DirectionsApiService
) : ViewModel() {

    var homeLocation by mutableStateOf<LatLng?>(null)
    var currentLocation by mutableStateOf<LatLng?>(null)
    var routePoints by mutableStateOf<List<LatLng>>(emptyList())

    init {
        viewModelScope.launch {
            homeLocation = db.homeLocationDao().get()?.toLatLng()
        }
    }

    fun saveHomeLocation(location: LatLng) {
        viewModelScope.launch {
            db.homeLocationDao().insert(HomeLocationEntity(1, location.latitude, location.longitude))
            homeLocation = location
        }
    }

    fun getRoute() {
        viewModelScope.launch {
            if (currentLocation == null || homeLocation == null) return@launch
            val body = DirectionsRequest.fromLatLngs(currentLocation!!, homeLocation!!)
            val response = api.getRoute(body)
            routePoints = PolylineDecoder.decode(response.body()?.features?.first()?.geometry?.coordinates)
        }
    }
}

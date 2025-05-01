package com.example.prueba5g

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkApp()
        }
    }
}

@Composable
fun NetworkApp() {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            launcher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    if (permissionGranted) {
        NetworkInfoScreen(context)
    } else {
        Column(Modifier.padding(16.dp)) {
            Text("Se necesita permiso para mostrar el tipo de red.")
        }
    }
}

@Composable
fun NetworkInfoScreen(context: Context) {
    var networkType by remember { mutableStateOf(getNetworkType(context)) }
    var pingResult by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tipo de red: $networkType")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                scope.launch {
                    pingResult = withContext(Dispatchers.IO) {
                        pingGoogle()
                    }
                }
            }) {
                Text("Hacer Ping a Google")
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (pingResult.isNotEmpty()) {
                Text(pingResult)
            }
        }
    }
}


fun getNetworkType(context: Context): String {
    val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    return if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val networkType = telephonyManager.dataNetworkType
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE -> "2G"
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA -> "3G"
            TelephonyManager.NETWORK_TYPE_LTE -> "4G"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Desconocido"
            else -> "Otro"
        }
    } else {
        "Permiso no concedido"
    }
}

fun pingGoogle(): String {
    return try {
        val startTime = System.currentTimeMillis()
        val url = URL("https://www.google.com")
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        connection.requestMethod = "GET"
        connection.connect()
        val responseCode = connection.responseCode
        val duration = System.currentTimeMillis() - startTime
        if (responseCode == HttpURLConnection.HTTP_OK) {
            "Ping exitoso (200 OK) en $duration ms"
        } else {
            "Respuesta: $responseCode en $duration ms"
        }
    } catch (e: Exception) {
        "Error en el ping: ${e.message}"
    }
}
package com.example.firebaseapp

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest


class MainActivity : ComponentActivity() {

    private val TAG = "FCM_TOKEN"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this, "Permiso para notificaciones concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso para notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Solicitar permiso POST_NOTIFICATIONS si es Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "Token FCM: $token")  // <-- Aquí se imprime el token en Logcat
            } else {
                Log.e(TAG, "Error al obtener token FCM", task.exception)
            }
        }
    }
}
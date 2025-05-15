package com.example.firebaseapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        setContentView(textView)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                textView.text = "Token FCM:\n$token"
                // Puedes copiar este token y usarlo en el servidor
            } else {
                textView.text = "Error obteniendo token"
            }
        }
    }
}
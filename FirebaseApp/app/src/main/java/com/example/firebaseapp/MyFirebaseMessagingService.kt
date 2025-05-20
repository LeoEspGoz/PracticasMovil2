package com.example.firebaseapp


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    class MyFirebaseMessagingService : FirebaseMessagingService() {
        override fun onNewToken(token: String) {
            super.onNewToken(token)
            Log.d("FCM", "Nuevo token: $token")
            // Aquí puedes enviar el token al servidor si lo deseas
        }

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)
            remoteMessage.notification?.let {
                showNotification(it.title, it.body)
            }
        }

        private fun showNotification(title: String?, message: String?) {
            val channelId = "fcm_default"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "FCM Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title ?: "Notificación")
                .setContentText(message ?: "")
                .build()

            notificationManager.notify(1, notification)
        }
    }
}
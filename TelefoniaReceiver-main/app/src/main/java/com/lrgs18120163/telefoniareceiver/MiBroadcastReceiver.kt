package com.lrgs18120163.telefoniareceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class MiBroadcastReceiver : BroadcastReceiver() {
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var lastIncomingNumber: String? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            val state = when (stateStr) {
                TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
                TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
                TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
                else -> return
            }

            Log.d("MiBroadcastReceiver", "Estado actual: $state, Último estado: $lastState")

            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val savedNumber = sharedPreferences.getString("numero", "")
            val message = sharedPreferences.getString("mensaje", "")

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                lastIncomingNumber = incomingNumber
                Log.d("MiBroadcastReceiver", "Llamada entrante detectada: $incomingNumber")
            }

            // Detectar llamada perdida
            if (state == TelephonyManager.CALL_STATE_IDLE && lastState == TelephonyManager.CALL_STATE_RINGING) {
                Log.d("MiBroadcastReceiver", "Llamada perdida detectada de: $lastIncomingNumber")
                if (lastIncomingNumber == savedNumber) {
                    Log.d("MiBroadcastReceiver", "Número coincide con el guardado, enviando SMS...")
                    sendSMS(context, savedNumber, message)
                } else {
                    Log.d("MiBroadcastReceiver", "Número NO coincide: Guardado -> $savedNumber, Recibido -> $lastIncomingNumber")
                }
            }

            lastState = state
        }
    }

    private fun sendSMS(context: Context, numero: String?, mensaje: String?) {
        Log.d("MiBroadcastReceiver", "Intentando enviar SMS a $numero con mensaje: $mensaje")

        if (!numero.isNullOrEmpty() && !mensaje.isNullOrEmpty()) {
            try {
                val sentIntent = PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE)
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(numero, null, mensaje, sentIntent, null)
                Toast.makeText(context, "Mensaje enviado a $numero", Toast.LENGTH_SHORT).show()
                Log.d("MiBroadcastReceiver", "Mensaje enviado correctamente a $numero")
            } catch (e: Exception) {
                Log.e("MiBroadcastReceiver", "Error al enviar SMS: ${e.message}")
                Toast.makeText(context, "Error al enviar SMS", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("MiBroadcastReceiver", "Número o mensaje vacío, no se envía SMS")
        }
    }
}

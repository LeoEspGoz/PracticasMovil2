package com.example.proyectodivisa.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.proyectodivisa.api.RetrofitClient
import com.example.proyectodivisa.dao.ActualizacionDao
import com.example.proyectodivisa.dao.DivizaDao
import com.example.proyectodivisa.database.AppDatabase
import com.example.proyectodivisa.entities.Actualizacion
import com.example.proyectodivisa.entities.Diviza

class UpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(applicationContext)
            val divizaDao = db.divizaDao()
            val actualizacionDao = db.actualizacionDao()

            val response = RetrofitClient.api.getExchangeRates().execute()

            if (response.isSuccessful) {
                val apiResponse = response.body()

                if (apiResponse != null) {
                    // Guardar nuevas tasas de cambio en la base de datos
                    val divizas = apiResponse.toDivizaList()
                    divizaDao.insertDivizas(divizas)

                    // Guardar la nueva información de actualización
                    val actualizacion = apiResponse.toActualizacion()
                    actualizacionDao.insertActualizacion(actualizacion)

                    Log.d("UpdateWorker", "Actualización exitosa: ${actualizacion.timeNextUpdateUtc}")
                }
            } else {
                Log.e("UpdateWorker", "Error en la API: ${response.errorBody()?.string()}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("UpdateWorker", "Error en el Worker: ${e.message}")
            Result.retry()
        }
    }

    // Función para convertir la respuesta de la API a una lista de Diviza
    private fun com.example.proyectodivisa.api.ApiResponse.toDivizaList(): List<Diviza> {
        return this.conversion_rates.map { (currency, rate) -> Diviza(currency, rate) }
    }

    // Función para convertir la respuesta de la API a una entidad Actualizacion
    private fun com.example.proyectodivisa.api.ApiResponse.toActualizacion(): Actualizacion {
        return Actualizacion(
            timeLastUpdateUnix = this.time_last_update_unix,
            timeLastUpdateUtc = this.time_last_update_utc,
            timeNextUpdateUnix = this.time_next_update_unix,
            timeNextUpdateUtc = this.time_next_update_utc,
            baseCode = this.base_code,
            result = this.result,
            documentation = this.documentation,
            termsOfUse = this.terms_of_use
        )
    }
}

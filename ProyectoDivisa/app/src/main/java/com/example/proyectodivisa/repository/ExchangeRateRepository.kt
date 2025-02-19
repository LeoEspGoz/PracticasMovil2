package com.example.proyectodivisa.repository

import android.util.Log
import com.example.proyectodivisa.api.RetrofitClient
import com.example.proyectodivisa.dao.ActualizacionDao
import com.example.proyectodivisa.dao.DivizaDao
import com.example.proyectodivisa.entities.Actualizacion
import com.example.proyectodivisa.entities.Diviza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExchangeRateRepository(
    private val divizaDao: DivizaDao,
    private val actualizacionDao: ActualizacionDao
) {

    suspend fun fetchAndSaveExchangeRates() {
        withContext(Dispatchers.IO) {
            val response = RetrofitClient.api.getExchangeRates().execute()
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->

                    // Insertar datos de Actualización
                    val actualizacion = Actualizacion(
                        id = 0,
                        timeLastUpdateUnix = apiResponse.time_last_update_unix,
                        timeLastUpdateUtc = apiResponse.time_last_update_utc,
                        timeNextUpdateUnix = apiResponse.time_next_update_unix,
                        timeNextUpdateUtc = apiResponse.time_next_update_utc,
                        baseCode = apiResponse.base_code,
                        result = apiResponse.result,
                        documentation = apiResponse.documentation,
                        termsOfUse = apiResponse.terms_of_use
                    )
                    actualizacionDao.insertActualizacion(actualizacion)

                    // Insertar datos de Divizas
                    val divizas = apiResponse.conversion_rates.map { (codigo, valor) ->
                        Diviza(codigo = codigo, valor = valor)
                    }
                    divizas.forEach { diviza ->
                        divizaDao.insertDiviza(diviza)
                    }
                }
            } else {
                Log.e("ExchangeRateRepository", "Error al obtener datos: ${response.errorBody()}")
            }
        }
    }
}

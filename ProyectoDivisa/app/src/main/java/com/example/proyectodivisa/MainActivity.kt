package com.example.proyectodivisa

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodivisa.adapter.DivizaAdapter
import com.example.proyectodivisa.api.ApiResponse
import com.example.proyectodivisa.api.RetrofitClient
import com.example.proyectodivisa.dao.ActualizacionDao
import com.example.proyectodivisa.dao.DivizaDao
import com.example.proyectodivisa.database.AppDatabase
import com.example.proyectodivisa.entities.Actualizacion
import com.example.proyectodivisa.entities.Diviza
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.work.*
import com.example.proyectodivisa.workers.UpdateWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var divizaDao: DivizaDao
    private lateinit var actualizacionDao: ActualizacionDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewJson: TextView
    private lateinit var textViewLastUpdate: TextView
    private lateinit var textViewNextUpdate: TextView
    private lateinit var divizaAdapter: DivizaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar TextView para mostrar el JSON y las fechas de actualización
        textViewJson = findViewById(R.id.textViewJson)
        textViewLastUpdate = findViewById(R.id.textViewLastUpdate)
        textViewNextUpdate = findViewById(R.id.textViewNextUpdate)

        // Inicializar el adaptador
        divizaAdapter = DivizaAdapter(emptyList())
        recyclerView.adapter = divizaAdapter

        // Obtener instancia de la base de datos
        val db = AppDatabase.getDatabase(this)
        divizaDao = db.divizaDao()
        actualizacionDao = db.actualizacionDao()

        // Verificar conexión a Internet y cargar datos
        lifecycleScope.launch {
            if (hayInternet()) {
                fetchAndDisplayApiData()
            } else {
                loadAndDisplayDivizas()

                val actualizacion = withContext(Dispatchers.IO) {
                    actualizacionDao.getLatestActualizacion()
                }

                withContext(Dispatchers.Main) {
                    if (actualizacion != null) {
                        textViewLastUpdate.text = "Última actualización: ${actualizacion.timeLastUpdateUtc}"
                        textViewNextUpdate.text = "Próxima actualización: ${actualizacion.timeNextUpdateUtc}"
                    }
                }
            }
        }
    }

    private fun hayInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun fetchAndDisplayApiData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getExchangeRates().execute()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        // Insertar datos en la base de datos
                        val divizas = apiResponse.toDivizaList()
                        insertOrUpdateDivizas(divizas)

                        val actualizacion = apiResponse.toActualizacion()
                        actualizacionDao.insertActualizacion(actualizacion)

                        withContext(Dispatchers.Main) {
                            // Mostrar datos en la UI
                            textViewJson.text = apiResponse.toString()
                            divizaAdapter.updateData(divizas)

                            textViewLastUpdate.text = "Última actualización: ${actualizacion.timeLastUpdateUtc}"
                            textViewNextUpdate.text = "Próxima actualización: ${actualizacion.timeNextUpdateUtc}"

                            scheduleUpdateWorker(actualizacion.timeNextUpdateUtc)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error en la API: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun scheduleUpdateWorker(timeNextUpdateUtc: String) {
        try {
            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'UTC'", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val nextUpdateTime: Date = dateFormat.parse(timeNextUpdateUtc) ?: return

            val currentTimeMillis = System.currentTimeMillis()
            val delayMillis = nextUpdateTime.time - currentTimeMillis

            if (delayMillis > 0) {
                val workRequest = OneTimeWorkRequestBuilder<UpdateWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()

                WorkManager.getInstance(this).enqueue(workRequest)
                Toast.makeText(this, "Worker programado en $timeNextUpdateUtc", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun insertOrUpdateDivizas(divizas: List<Diviza>) {
        withContext(Dispatchers.IO) {
            divizas.forEach { diviza ->
                divizaDao.insertDiviza(diviza)
            }
        }
    }

    private fun loadAndDisplayDivizas() {
        lifecycleScope.launch(Dispatchers.IO) {
            val divizas = divizaDao.getAllDivizas()
            withContext(Dispatchers.Main) {
                if (divizas.isNotEmpty()) {
                    divizaAdapter.updateData(divizas)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No hay datos en la base de datos.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun ApiResponse.toDivizaList(): List<Diviza> {
        return this.conversion_rates.map { (currency, rate) ->
            Diviza(currency, rate)
        }
    }

    private fun ApiResponse.toActualizacion(): Actualizacion {
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

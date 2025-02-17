package com.example.proyectodivisas.Repository

import androidx.lifecycle.LiveData
import com.example.proyectodivisas.Data.ExchangeRate
import com.example.proyectodivisas.Data.ExchangeRateApi
import com.example.proyectodivisas.Data.ExchangeRateDao

class ExchangeRateRepository(private val dao: ExchangeRateDao, private val api: ExchangeRateApi) {

    suspend fun fetchAndStoreExchangeRates() {
        val response = api.getExchangeRates()
        if (response.isSuccessful) {
            response.body()?.let { data ->
                val ratesList = data.conversion_rates.map { (currency, rate) ->
                    ExchangeRate(
                        baseCurrency = data.base_code,
                        targetCurrency = currency,
                        rate = rate,
                        timestamp = data.time_last_update_unix
                    )
                }
                dao.insertAll(ratesList)
            }
        }
    }

    fun getRates(baseCurrency: String): LiveData<List<ExchangeRate>> = dao.getRatesByBase(baseCurrency)
}

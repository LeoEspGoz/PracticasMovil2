package com.example.proyectodivisas.Data
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v6/e04fda6110e3c39cb509b3d2/latest/MXN")
    suspend fun getExchangeRates(): Response<ExchangeRateResponse>
}
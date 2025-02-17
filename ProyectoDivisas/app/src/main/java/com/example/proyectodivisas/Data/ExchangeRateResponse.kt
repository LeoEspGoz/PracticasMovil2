package com.example.proyectodivisas.Data

data class ExchangeRateResponse(
    val result: String,
    val time_last_update_unix: Long,
    val time_last_update_utc: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>
)


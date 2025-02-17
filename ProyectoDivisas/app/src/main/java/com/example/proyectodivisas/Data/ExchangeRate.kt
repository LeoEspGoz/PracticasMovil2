package com.example.proyectodivisas.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val timestamp: Long
)
package com.example.proyectodivisas.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ExchangeRate>)

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :base ORDER BY timestamp DESC")
    fun getRatesByBase(base: String): LiveData<List<ExchangeRate>>
}
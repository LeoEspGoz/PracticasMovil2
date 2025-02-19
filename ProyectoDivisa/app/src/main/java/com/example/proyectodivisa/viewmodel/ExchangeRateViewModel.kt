package com.example.proyectodivisa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodivisa.repository.ExchangeRateRepository
import kotlinx.coroutines.launch

class ExchangeRateViewModel(private val repository: ExchangeRateRepository) : ViewModel() {

    fun fetchAndSaveExchangeRates() {
        viewModelScope.launch {
            repository.fetchAndSaveExchangeRates()
        }
    }
}

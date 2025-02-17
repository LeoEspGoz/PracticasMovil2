package com.example.proyectodivisas.viewModel

import androidx.lifecycle.*
import com.example.proyectodivisas.Data.ExchangeRate
import com.example.proyectodivisas.Repository.ExchangeRateRepository
import kotlinx.coroutines.launch

class ExchangeRateViewModel(private val repository: ExchangeRateRepository) : ViewModel() {

    val exchangeRates: LiveData<List<ExchangeRate>> = repository.getRates("USD")

    fun syncRates() {
        viewModelScope.launch {
            repository.fetchAndStoreExchangeRates()
        }
    }
}
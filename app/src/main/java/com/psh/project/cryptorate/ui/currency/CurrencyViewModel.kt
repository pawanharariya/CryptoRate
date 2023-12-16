package com.psh.project.cryptorate.ui.currency

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.psh.project.cryptorate.data.model.Currency
import com.psh.project.cryptorate.data.model.Result.Error
import com.psh.project.cryptorate.data.model.Result.Success
import com.psh.project.cryptorate.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val repository: CryptoRepository) :
    ViewModel() {
    private val cryptoMap = MutableLiveData<Map<String, Currency>?>()

    val cryptoList: LiveData<List<Currency>> = cryptoMap.map {
        it?.values?.toList() ?: emptyList()
    }

    val liveRateMap = MutableLiveData<Map<String, Double>?>()

    init {
        viewModelScope.launch {
            when (val response = repository.getCurrencyList()) {
                is Success -> cryptoMap.value = response.data
                is Error -> Log.e("API RESPONSE ---->>>", response.message ?: "no msg")

            }
            when (val response = repository.getLiveRates()) {
                is Success -> {
                    liveRateMap.value = response.data
                    updateCryptoListWithLiveRate()
                }

                is Error -> Log.e("API RESPONSE ---->>>", response.message ?: "no msg")
            }
        }
    }

    private fun updateCryptoListWithLiveRate() {
        liveRateMap.value?.let {
            for (item in it) {
                cryptoMap.value?.get(item.key)?.exchangeRate = item.value.toString()
            }
        }
    }
}
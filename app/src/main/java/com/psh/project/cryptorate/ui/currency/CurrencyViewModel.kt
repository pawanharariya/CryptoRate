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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val repository: CryptoRepository) :
    ViewModel() {
    private val cryptoMap = MutableLiveData<Map<String, Currency>?>()

    /**
     * Covert currency map to list for adapter
     */
    val cryptoList: LiveData<List<Currency>> = cryptoMap.map {
        it?.values?.toList() ?: emptyList()
    }

    private val _liveRateMap = MutableLiveData<Map<String, Double>?>()
    val liveRateMap: LiveData<Map<String, Double>?> = _liveRateMap

    private var repeatedLiveRateFetchJob: Job? = null

    init {
        viewModelScope.launch {
            when (val response = repository.getCurrencyList()) {
                is Success -> cryptoMap.value = response.data
                is Error -> Log.e("API RESPONSE ---->>>", response.message ?: "no msg")
            }
            startLiveRateFetching()
        }
    }

    private fun updateCryptoListWithLiveRate() {
        liveRateMap.value?.let {
            for (item in it) {
                cryptoMap.value?.get(item.key)?.exchangeRate = item.value.toString()
            }
        }
    }

    fun startLiveRateFetching() {
        if (repeatedLiveRateFetchJob?.isActive == true) {
            Log.e("API","fetching job already active")
            return
        }
        repeatedLiveRateFetchJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                fetchLiveRates()
                delay(LIVE_RATE_FETCHING_DELAY)
            }
        }
    }

    fun stopLiveRateFetching() {
        if (repeatedLiveRateFetchJob?.isActive == true) {
            Log.e("API","fetching job cancelled")
            repeatedLiveRateFetchJob?.cancel()
        }
    }

    private suspend fun fetchLiveRates() {
        Log.e("API RESPONSE", "---->>>Fetching live rate")
        when (val response = repository.getLiveRates()) {
            is Success -> {
                _liveRateMap.postValue(response.data)
                updateCryptoListWithLiveRate()
            }

            is Error -> Log.e("API RESPONSE ---->>>", response.message ?: "no msg")
        }
    }

    fun forceRefreshLiveRates() {
        viewModelScope.launch {
            fetchLiveRates()
        }
    }
}

/**
 * Fetching live rate every 3 minutes
 */
private const val LIVE_RATE_FETCHING_DELAY = 3 * 60 * 1000L
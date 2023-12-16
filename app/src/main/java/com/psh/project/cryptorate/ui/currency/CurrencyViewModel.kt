package com.psh.project.cryptorate.ui.currency

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.psh.project.cryptorate.Event
import com.psh.project.cryptorate.data.CryptoRepository
import com.psh.project.cryptorate.data.model.Currency
import com.psh.project.cryptorate.data.model.Result.Error
import com.psh.project.cryptorate.data.model.Result.Success
import com.psh.project.cryptorate.utils.SnackbarData
import com.psh.project.cryptorate.utils.SnackbarType
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
    private val currencyMap = MutableLiveData<Map<String, Currency>?>()

    /**
     * Covert currency map to list for adapter
     */
    val currencyList: LiveData<List<Currency>> = currencyMap.map {
        it?.values?.toList() ?: emptyList()
    }

    private val _liveRateMap = MutableLiveData<Map<String, Double>?>()
    val liveRateMap: LiveData<Map<String, Double>?> = _liveRateMap

    private var repeatedLiveRateFetchJob: Job? = null

    /**
     * LiveData for all snackbar events
     */
    private val _snackbarEvent = MutableLiveData<Event<SnackbarData>>()
    val snackbarEvent: LiveData<Event<SnackbarData>> = _snackbarEvent

    init {
        fetchCurrencyList()
    }

    private fun fetchCurrencyList() {
        viewModelScope.launch {
            when (val response = repository.getCurrencyList()) {
                is Success -> currencyMap.value = response.data
                is Error -> _snackbarEvent.postValue(
                    Event(
                        SnackbarData(
                            response.message,
                            SnackbarType.ERROR
                        ) { retryCurrencyFetch() })
                )
            }
            forceRefreshLiveRates()
        }
    }

    private fun updateCurrencyListWithLiveRate() {
        liveRateMap.value?.let {
            for (item in it) {
                currencyMap.value?.get(item.key)?.exchangeRate = item.value.toString()
            }
        }
    }

    fun startLiveRateFetching() {
        if (repeatedLiveRateFetchJob?.isActive == true) {
            Log.e("API", "fetching job already active")
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
            Log.e("API", "fetching job cancelled")
            repeatedLiveRateFetchJob?.cancel()
        }
    }

    private suspend fun fetchLiveRates() {
        Log.e("API RESPONSE", "---->>>Fetching live rate")
        when (val response = repository.getLiveRates()) {
            is Success -> {
                _liveRateMap.postValue(response.data)
                updateCurrencyListWithLiveRate()
            }

            is Error -> _snackbarEvent.postValue(
                Event(SnackbarData(response.message, SnackbarType.ERROR) { retryCurrencyFetch() })
            )
        }
    }

    fun forceRefreshLiveRates() {
        viewModelScope.launch {
            fetchLiveRates()
        }
    }

    private fun retryCurrencyFetch() {
        fetchCurrencyList()
    }
}

/**
 * Fetching live rate every 3 minutes
 */
private const val LIVE_RATE_FETCHING_DELAY = 3 * 60 * 1000L
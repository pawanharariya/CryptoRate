package com.psh.project.cryptorate.ui.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psh.project.cryptorate.Event
import com.psh.project.cryptorate.data.CryptoRepository
import com.psh.project.cryptorate.data.model.Currency
import com.psh.project.cryptorate.data.model.Result.Error
import com.psh.project.cryptorate.data.model.Result.Success
import com.psh.project.cryptorate.data.prefs.SharedPreferencesManager
import com.psh.project.cryptorate.utils.SnackbarData
import com.psh.project.cryptorate.utils.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) :
    ViewModel() {
    private val _currencyList = MutableLiveData<List<Currency>?>()
    val currencyList: LiveData<List<Currency>?> = _currencyList

    private val _liveRateMap = MutableLiveData<Map<String, Double>?>()
    val liveRateMap: LiveData<Map<String, Double>?> = _liveRateMap

    private var repeatedLiveRateFetchJob: Job? = null

    private val _lastRefreshTime = MutableLiveData<String>()
    val lastRefreshTime: LiveData<String> = _lastRefreshTime

    /**
     * LiveData for all snackbar events
     */
    private val _snackbarEvent = MutableLiveData<Event<SnackbarData>>()
    val snackbarEvent: LiveData<Event<SnackbarData>> = _snackbarEvent

    init {
        fetchCurrencyList()
        _lastRefreshTime.value = sharedPreferencesManager.getLastFetchedTime()
    }

    private fun fetchCurrencyList() {
        viewModelScope.launch {
            when (val response = repository.getCurrencyList()) {
                is Success -> _currencyList.postValue(response.data)
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
        liveRateMap.value?.let { ratesMap ->
            _currencyList.value?.let {
                for (item in it) {
                    item.exchangeRate = ratesMap[item.symbol] ?: 0.0
                }
            }
        }
    }

    fun startLiveRateFetching() {
        if (repeatedLiveRateFetchJob?.isActive == true) {
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
            repeatedLiveRateFetchJob?.cancel()
        }
    }

    private suspend fun fetchLiveRates() {
        when (val response = repository.getLiveRatesFromNetwork()) {
            is Success -> {
                _liveRateMap.postValue(response.data)
                updateCurrencyListWithLiveRate()
                updateLastFetchedTime()
            }

            is Error -> _snackbarEvent.postValue(
                Event(SnackbarData(response.message, SnackbarType.ERROR) { retryCurrencyFetch() })
            )
        }
    }

    private fun updateLastFetchedTime() {
        val currentDate = Date()
        val formatter = SimpleDateFormat("HH:mm a", Locale.US)
        val formattedDateTime = formatter.format(currentDate)
        _lastRefreshTime.postValue(formattedDateTime)
        sharedPreferencesManager.saveLastFetchedTime(formattedDateTime)
    }

    fun forceRefreshLiveRates() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchLiveRates()
        }
    }

    private fun retryCurrencyFetch() {
        fetchCurrencyList()
    }

}

/**
 * Fetching live rates every 3 minutes
 */
private const val LIVE_RATE_FETCHING_DELAY = 3 * 60 * 1000L
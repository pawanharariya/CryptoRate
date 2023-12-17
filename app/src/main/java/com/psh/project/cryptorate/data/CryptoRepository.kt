package com.psh.project.cryptorate.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import com.psh.project.cryptorate.R
import com.psh.project.cryptorate.data.local.CurrencyDao
import com.psh.project.cryptorate.data.local.CurrencyDatabase
import com.psh.project.cryptorate.data.model.Currency
import com.psh.project.cryptorate.data.model.Result
import com.psh.project.cryptorate.data.model.Result.Error
import com.psh.project.cryptorate.data.model.Result.Success
import com.psh.project.cryptorate.data.remote.CryptoService
import javax.inject.Inject

class CryptoRepository @Inject constructor(
    private val cryptoService: CryptoService,
    private val currencyDao: CurrencyDao,
    private val application: Application
) {
    suspend fun getCurrencyList(forceUpdate: Boolean = false): Result<List<Currency>> {
        if (forceUpdate)
            return fetchCurrencyList()
        val currencyList = currencyDao.getCurrencyList()
        return if (currencyList.isNotEmpty())
            Success(currencyList)
        else fetchCurrencyList()
    }

    private suspend fun fetchCurrencyList(): Result<List<Currency>> {
        if (!hasInternetConnection()) {
            return Error(application.getString(R.string.error_no_internet))
        }
        return try {
            val response = cryptoService.getCryptoList()
            if (response.success && response.currencies != null) {
                currencyDao.deleteCurrencies()
               currencyDao.saveCurrencyList(response.currencies.values.toList())
                Success(response.currencies.values.toList())
            } else Error(response.error?.info)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e.message)
        }
    }

    suspend fun getLiveRatesFromNetwork(): Result<Map<String,Double>> {
        if (!hasInternetConnection()) {
            return Error(application.getString(R.string.error_no_internet))
        }
        return try {
            val response = cryptoService.getLiveRates()
            if (response.success && response.rates != null) {
                currencyDao.updateLiveRatesBatch(response.rates)
                Success(response.rates)
            } else Error(response.error?.info)
        } catch (e: Exception) {
            Error(e.message)
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
package com.psh.project.cryptorate.repository

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import com.psh.project.cryptorate.R
import com.psh.project.cryptorate.data.CryptoService
import com.psh.project.cryptorate.data.model.Currency
import com.psh.project.cryptorate.data.model.Result
import com.psh.project.cryptorate.data.model.Result.Error
import com.psh.project.cryptorate.data.model.Result.Success
import javax.inject.Inject

class CryptoRepository @Inject constructor(
    private val cryptoService: CryptoService,
    private val application: Application
) {
    suspend fun getCurrencyList(): Result<MutableMap<String, Currency>> {
        if (!hasInternetConnection()) {
            return Error(application.getString(R.string.error_no_internet))
        }
        return try {
            val response = cryptoService.getCryptoList()
            if (response.success && response.currencies != null) {
                Success(response.currencies)
            } else Error(response.error?.info)
        } catch (e: Exception) {
            Error(e.message)
        }
    }

    suspend fun getLiveRates(): Result<MutableMap<String, Double>> {
        if (!hasInternetConnection()) {
            return Error(application.getString(R.string.error_no_internet))
        }
        return try {
            val response = cryptoService.getLiveRates()
            if (response.success && response.rates != null) {
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
package com.psh.project.cryptorate.data.remote

import com.psh.project.cryptorate.data.model.CurrencyListResponse
import com.psh.project.cryptorate.data.model.LiveRateResponse
import com.psh.project.cryptorate.utils.Constants
import retrofit2.http.GET

interface CryptoService {

    @GET("live?access_key=${Constants.API_KEY}")
    suspend fun getLiveRates(): LiveRateResponse

    @GET("list?access_key=${Constants.API_KEY}")
    suspend fun getCryptoList(): CurrencyListResponse
}
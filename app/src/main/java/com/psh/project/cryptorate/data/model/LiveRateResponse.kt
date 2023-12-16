package com.psh.project.cryptorate.data.model

import com.google.gson.annotations.SerializedName

data class LiveRateResponse(
    /**
     * Whether response is success or not
     */
    val success: Boolean,

    /**
     * Map of Crypto Symbol to live rate in USD
     */
    @SerializedName("rates")
    val rates: MutableMap<String, Double>?,

    /**
     * Received in case of Error
     * Null when [success] = true
     */
    val error: Error?
)

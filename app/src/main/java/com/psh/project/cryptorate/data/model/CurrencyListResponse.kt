package com.psh.project.cryptorate.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyListResponse(
    /**
     * Whether response is success or not
     */
    val success: Boolean,

    /**
     * Map of Crypto Symbol to Crypto [Currency]
     */
    @SerializedName("crypto")
    val currencies: Map<String, Currency>?,

    /**
     * Received in case of Error
     * Null when [success] = true
     */
    val error: Error?
)

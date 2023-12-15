package com.psh.project.cryptorate.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyListResponse(
    /**
     * Whether its success response or error response
     */
    val success : Boolean,

    
    /**
     * Received in case of Error
     * Null when [success] = true
     */
    val error : Error?
)

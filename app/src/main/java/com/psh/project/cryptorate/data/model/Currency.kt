package com.psh.project.cryptorate.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Currency(
    /**
     * Symbol of Crypto Currency, works as unique ID
     */
    val symbol: String,

    /**
     * Full name of the Crypto Currency
     */
    @Json(name = "name_full")
    val name: String,

    /**
     * Url of the icon
     */
    @Json(name = "icon_url")
    val iconUrl: String,

    /**
     * Exchange rate in USD
     */
    val exchangeRate: String?
)
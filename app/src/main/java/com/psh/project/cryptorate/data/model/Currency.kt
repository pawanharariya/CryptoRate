package com.psh.project.cryptorate.data.model

import com.google.gson.annotations.SerializedName

data class Currency(
    /**
     * Symbol of Crypto Currency, works as unique ID
     */
    val symbol: String,

    /**
     * Full name of the Crypto Currency
     */
    @SerializedName("name_full")
    val name: String,

    /**
     * Url of the icon
     */
    @SerializedName("icon_url")
    val iconUrl: String,

    /**
     * Exchange rate in USD
     */
    var exchangeRate: String = ""
)
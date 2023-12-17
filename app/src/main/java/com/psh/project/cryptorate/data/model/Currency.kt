package com.psh.project.cryptorate.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "currency")
data class Currency(
    /**
     * Symbol of Crypto Currency, works as unique ID
     */
    @PrimaryKey
    val symbol: String ,

    /**
     * Name of the Crypto Currency
     */
    val name: String,

    /**
     * Url of the icon
     */
    @ColumnInfo(name = "icon_url")
    @SerializedName("icon_url")
    val iconUrl: String,

    /**
     * Exchange rate in USD
     */
    @ColumnInfo(name = "exchange_rate")
    var exchangeRate: Double = 0.0
)
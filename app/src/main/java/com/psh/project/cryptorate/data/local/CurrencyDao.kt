package com.psh.project.cryptorate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.psh.project.cryptorate.data.model.Currency

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency")
    suspend fun getCurrencyList(): List<Currency>

    @Insert(onConflict = REPLACE)
    suspend fun saveCurrencyList(list: List<Currency>)

    @Query("DELETE FROM currency")
    suspend fun deleteCurrencies()

//    @Query("SELECT symbol, exchange_rate FROM currency")
//    fun getLiveRateMap(): Map<@MapColumn(columnName = "symbol") String,
//            @MapColumn(columnName = "exchange_rate") Double>

    @Query("UPDATE currency SET exchange_rate = :rate WHERE symbol = :symbol")
    suspend fun updateRatings(rate: Double, symbol: String)

    suspend fun updateLiveRatesBatch(liveRateMap: Map<String, Double>) {
        for ((id, rating) in liveRateMap) {
            updateRatings(rating, id)
        }
    }
}
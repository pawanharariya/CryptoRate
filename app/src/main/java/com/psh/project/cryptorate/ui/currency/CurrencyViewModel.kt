package com.psh.project.cryptorate.ui.currency

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psh.project.cryptorate.data.model.Currency
import com.psh.project.cryptorate.data.model.Result
import com.psh.project.cryptorate.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val repository: CryptoRepository) :
    ViewModel() {

        val cryptoList = MutableLiveData<Map<String, Currency>?>()
    init {
        viewModelScope.launch {
            Log.e("API", "start fetching")
            when(val response = repository.getCurrencyList()){
                is Result.Success ->{
                    Log.e("API RESPONSE ---->>>", response.data!!.size.toString())
                    cryptoList.value = response.data
                }
                else -> {Log.e("API RESPONSE ---->>>", response.message ?: "no msg")}
            }

        }
    }
}
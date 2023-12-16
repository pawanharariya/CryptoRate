package com.psh.project.cryptorate.ui.currency

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.psh.project.cryptorate.data.model.Currency
import com.psh.project.cryptorate.databinding.ListItemCurrencyBinding
import com.psh.project.cryptorate.ui.currency.CurrencyAdapter.CurrencyViewHolder

class CurrencyAdapter : ListAdapter<Currency, CurrencyViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }
    }

    inner class CurrencyViewHolder(private val binding: ListItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: Currency) {
            binding.currency = currency
            binding.executePendingBindings()
        }

        fun updateRating(liveRating: Double) {
            binding.rate.text = liveRating.toString();
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(ListItemCurrencyBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        Log.e("Adapter", "Adapter inside plain onBindViewHolder")
        val currency = getItem(position)
        holder.bind(currency)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int, payload: List<Any>) {
        val currency = getItem(position)
        Log.e("Adapter", "Adapter inside payload onBindViewHolder")
        if(payload.isEmpty()) {
            holder.bind(currency)
        }
        else holder.updateRating(payload[0] as Double)
    }

    fun updateLiveRating(liveRate : Map<String, Double>){
        for(i in 0..<itemCount){
            val item = getItem(i)
            notifyItemChanged(i, liveRate[item.symbol])
        }
    }
}
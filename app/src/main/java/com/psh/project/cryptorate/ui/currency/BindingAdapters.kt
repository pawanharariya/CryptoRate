package com.psh.project.cryptorate.ui.currency

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("currencyImage")
fun bindCurrencyImage(imageView: ImageView, iconUrl: String) {
    Log.e("API", "image refreshed")
    // TODO picasso error handling loading and optimisation
    Picasso.get().load(iconUrl).into(imageView)
}

@BindingAdapter("exchangeRate")
fun exchangeRate(textView: TextView, exchangeRate: Double) {
    Log.e("API", "rate refreshed")

    if(exchangeRate == 0.0)
        return
    val rounded = String.format("%.6f", exchangeRate)
    textView.text = "\$ $rounded"
}
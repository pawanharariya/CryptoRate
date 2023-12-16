package com.psh.project.cryptorate.ui.currency

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("currencyImage")
fun bindCurrencyImage(imageView: ImageView, iconUrl: String) {

    // TODO picasso error handling loading and optimisation
    Picasso.get().load(iconUrl).into(imageView)
}
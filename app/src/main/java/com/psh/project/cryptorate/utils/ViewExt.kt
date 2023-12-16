package com.psh.project.cryptorate.utils

import android.support.annotation.ColorRes
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.psh.project.cryptorate.R

fun View.snackbar(snackbarData: SnackbarData): Snackbar {
    val snackbar = Snackbar.make(
        this,
        snackbarData.message ?: resources.getString(R.string.unknown_error),
        Snackbar.LENGTH_LONG
    )
        .setBackgroundTint(ContextCompat.getColor(this.context, snackbarData.type.backgroundTint))
        .setTextColor(ContextCompat.getColor(this.context, snackbarData.type.textColor))
    snackbarData.action?.let {
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE)
        snackbar.setActionTextColor(ContextCompat.getColor(this.context, snackbarData.type.textColor))
        snackbar.setAction(resources.getString(R.string.snackbar_retry_text)) {
            it()
        }
    }
    snackbar.show()
    return snackbar
}

enum class SnackbarType(@ColorRes val backgroundTint: Int, @ColorRes val textColor: Int) {
    ERROR(R.color.snackbar_error, R.color.white),
    WARNING(R.color.snackbar_warning, R.color.white),
    SUCCESS(R.color.snackbar_success, R.color.white)
}
package com.psh.project.cryptorate.utils

data class SnackbarData(
    /**
     * Message shown on Snackbar
     */
    val message: String?,
    /**
     * [SnackbarType] type of message - success, error or warning
     */
    val type: SnackbarType,
    /**
     *  Action that can be performed by snackbar
     */
    val action: (() -> Unit)? = null
)
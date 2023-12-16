package com.psh.project.cryptorate.data.model

/**
 * A generic class that holds the network api response whether success or error
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String?) : Result<Nothing>()
}
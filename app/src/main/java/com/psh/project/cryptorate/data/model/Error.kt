package com.psh.project.cryptorate.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Error(
    val code: Int,
    val type: String,
    val info: String
)

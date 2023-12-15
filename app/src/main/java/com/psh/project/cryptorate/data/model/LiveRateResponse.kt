package com.psh.project.cryptorate.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LiveRateResponse(val name : String)

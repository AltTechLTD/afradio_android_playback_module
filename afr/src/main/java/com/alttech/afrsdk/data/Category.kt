package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 24/10/2017.
 */


data class Category(
    @Json(name = "key")
    var key: String?,
    @Json(name = "name")
    var name: String?
)

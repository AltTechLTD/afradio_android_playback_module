package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 20/10/2017.
 */

data class Playback(
    @Json(name = "_id")
    var id: String?,
    @Json(name = "length")
    var length: Int?,
    @Json(name = "monetize")
    var monetize: Boolean?,
    @Json(name = "plays")
    var plays: Int?,
    @Json(name = "sessionDate")
    var sessionDate: String?,
    @Json(name = "streamUrl")
    var streamUrl: String?
)

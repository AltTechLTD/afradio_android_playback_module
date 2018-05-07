package com.alttech.afrsdk.data

import com.squareup.moshi.Json

data class LoadMoreDataResult(
    @Json(name = "count")
    var count: Int,
    @Json(name = "limit")
    var limit: Int,
    @Json(name = "offsett")
    var offsett: Int,
    @Json(name = "playbacks")
    var playbacks: List<Playback>
)

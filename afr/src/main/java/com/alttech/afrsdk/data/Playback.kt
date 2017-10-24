package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 20/10/2017.
 */

class Playback {
  @Json(name = "_id")
  var id: String? = null
  @Json(name = "length")
  var length: Int? = null
  @Json(name = "monetize")
  var monetize: Boolean? = null
  @Json(name = "plays")
  var plays: Int? = null
  @Json(name = "sessionDate")
  var sessionDate: String? = null
  @Json(name = "streamUrl")
  var streamUrl: String? = null
}

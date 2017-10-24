package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 20/10/2017.
 */

class Schedule {
  @Json(name = "day")
  var day: Int? = null
  @Json(name = "duration")
  var duration: Int? = null
  @Json(name = "start")
  var start: Int? = null
}

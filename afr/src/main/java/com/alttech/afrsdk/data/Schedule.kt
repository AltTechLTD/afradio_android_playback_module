package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 20/10/2017.
 */

data class Schedule (
  @Json(name = "day")
  val day: Int?,
  @Json(name = "duration")
  val duration: Int?,
  @Json(name = "start")
  val start: Int?
)

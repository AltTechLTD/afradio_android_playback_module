package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 20/10/2017.
 */

class Show {
  @Json(name = "_id")
  var id: String? = null
  @Json(name = "category")
  var category: Category? = null
  @Json(name = "country")
  var country: String? = null
  @Json(name = "createdAt")
  var createdAt: String? = null
  @Json(name = "description")
  var description: String? = null
  @Json(name = "genres")
  var genres: List<Any>? = null
  @Json(name = "hashTag")
  var hashTag: String? = null
  @Json(name = "imgUrl")
  var imgUrl: String? = null
  @Json(name = "name")
  var name: String? = null
  @Json(name = "playback")
  var playback: List<Playback>? = null
  @Json(name = "schedules")
  var schedules: List<Schedule>? = null
  @Json(name = "subscribers")
  var subscribers: Int? = null
}

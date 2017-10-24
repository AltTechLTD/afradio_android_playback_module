package com.alttech.afrsdk.data

/**
 * Created by bubu on 20/10/2017.
 */

import com.squareup.moshi.Json

class Result {
  @Json(name = "_id")
  private val id: String? = null
  @Json(name = "cities")
  private val cities: List<String>? = null
  @Json(name = "country")
  private val country: String? = null
  @Json(name = "createdAt")
  private val createdAt: String? = null
  @Json(name = "description")
  private val description: String? = null
  @Json(name = "featured")
  private val featured: Boolean? = null
  @Json(name = "frequency")
  private val frequency: String? = null
  @Json(name = "genres")
  private val genres: List<String>? = null
  @Json(name = "imgUrl")
  private val imgUrl: String? = null
  @Json(name = "name")
  private val name: String? = null
  @Json(name = "shows")
  private val shows: List<Show>? = null
  @Json(name = "streamUrl")
  private val streamUrl: String? = null
}

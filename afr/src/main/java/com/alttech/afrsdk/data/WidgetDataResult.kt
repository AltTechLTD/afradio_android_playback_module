package com.alttech.afrsdk.data

/**
 * Created by bubu on 20/10/2017.
 */

import com.squareup.moshi.Json

data class WidgetDataResult(
    @Json(name = "_id")
    val id: String?,
    @Json(name = "cities")
    val cities: List<String?>?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "createdAt")
    val createdAt: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "featured")
    val featured: Boolean?,
    @Json(name = "frequency")
    val frequency: String?,
    @Json(name = "genres")
    val genres: List<String>?,
    @Json(name = "imgUrl")
    val imgUrl: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "shows")
    val shows: List<Show>?,
    @Json(name = "streamUrl")
    val streamUrl: String?
)

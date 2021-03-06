package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 20/10/2017.
 */

data class Show(
    @Json(name = "_id")
    val id: String?,
    @Json(name = "category")
    val category: Category?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "createdAt")
    val createdAt: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "genres")
    val genres: List<Any>?,
    @Json(name = "hashTag")
    val hashTag: String?,
    @Json(name = "imgUrl")
    val imgUrl: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "playback")
    val playback: List<Playback>?,
    @Json(name = "schedules")
    val schedules: List<Schedule>?,
    @Json(name = "subscribers")
    val subscribers: Int?
)

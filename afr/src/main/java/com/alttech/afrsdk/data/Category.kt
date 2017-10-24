package com.alttech.afrsdk.data

import com.squareup.moshi.Json

/**
 * Created by bubu on 24/10/2017.
 */


class Category {
  @Json(name = "key")
  var key: String? = null
  @Json(name = "name")
  var name: String? = null
}

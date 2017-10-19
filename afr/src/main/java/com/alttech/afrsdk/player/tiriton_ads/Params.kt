package com.alttech.afrsdk.player.tiriton_ads

/**
 * Created by bubu on 17/08/2017.
 */

class Params {

  var host = "cmod546.live.streamtheworld.com"
  var stationId = "12345"
  var countryCode: String? = null
  var bannerSize = "300x250"
  var yearOfBirth: String? = null
  var gender: GENDER? = null
  var latitude: Double? = null
  var longitude: Double? = null
  var gaid: String? =null
  var interests: Array<String>? =null

  enum class GENDER {
    MALE, FEMALE
  }

  class Builder {
    internal val params = Params()

    fun setHost(host: String): Builder {
      params.host = host
      return this
    }

    fun setStationId(stationId: String): Builder {
      params.stationId = stationId
      return this
    }

    fun setCountryCode(countryCode: String): Builder {
      params.countryCode = countryCode
      return this
    }

    fun setBannerSize(bannerSize: String): Builder {
      params.bannerSize = bannerSize
      return this
    }

    fun setYearBirth(year: String?): Builder {
      params.yearOfBirth = year
      return this
    }

    fun setGender(gender: GENDER): Builder {
      params.gender = gender
      return this
    }

    fun setLocation(lat: Double, long: Double): Builder {
      params.latitude = lat
      params.longitude = long
      return this
    }

    fun setGAID(gaid: String?): Builder {
      params.gaid = gaid
      return this
    }

    fun setInterests(interests: Array<String>): Builder {
      params.interests = interests
      return this
    }

    fun build() = params
  }
}

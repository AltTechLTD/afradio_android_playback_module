package com.alttech.afrsdk.network

/**
 * Created by bubu on 19/10/2017.
 */


import com.alttech.afrsdk.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import rx.Observable

/**
 * Created by bubu on 3/21/16.
 */
interface ApiService {

  @GET("api/widget")
  fun getShows(@Query("appId") order: String, @Query("resId") resId: String): Observable<>


  @FormUrlEncoded
  @PUT("api/user/fcm")
  fun updateFCM(
      @Field("fbuid") fbuid: String,
      @Field("fcmId") token: String): Observable<Response<Void>>

  /********
   * Util class that sets up a new services
   */
  object Creator {

    fun getApiService(): ApiService {


      return Retrofit.Builder()
          .baseUrl(if (staging()) STAGING_ENDPOINT else PRODUCTION_ENDPOINT)
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(MoshiConverterFactory.create())
          .build()
          .create(ApiService::class.java)
    }
  }

  companion object {

    const val PRODUCTION_ENDPOINT = "https://api.afrad.io/"
    const val STAGING_ENDPOINT = "http://stage.afradio.co/"

    const val AUTH_TOKEN = "x-auth-token"

    private var INSTANCE: ApiService? = null

    fun getInstance(): ApiService {
      if (INSTANCE == null)
        INSTANCE = ApiService.Creator.getApiService()
      return INSTANCE as ApiService
    }

    fun staging(): Boolean = BuildConfig.DEBUG
  }

}

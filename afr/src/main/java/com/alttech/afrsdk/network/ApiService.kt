package com.alttech.afrsdk.network

/**
 * Created by bubu on 19/10/2017.
 */


import com.alttech.afrsdk.BuildConfig
import com.alttech.afrsdk.data.LoadMoreDataResult
import com.alttech.afrsdk.data.WidgetDataResult
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by bubu on 3/21/16.
 */
interface ApiService {

  @GET("api/widget/station")
  fun getShows(@Query("appId") appId: String, @Query("resId") resId: String): Observable<WidgetDataResult>

  @GET("api/widget/loadmore")
  fun loadMore(@Query("appId") appId: String, @Query("show") showId: String, @Query("offsett") offset: Int, @Query("limit") limit: Int): Observable<LoadMoreDataResult>

  @GET("api/ad_credentials")
  fun getAdCredentials(): Observable<String>


  /********
   * Util class that sets up a new services
   */
  object Creator {

    fun getApiService(): ApiService {

      val moshi = Moshi.Builder()
          .add(KotlinJsonAdapterFactory())
          .build()

      val httpClient = OkHttpClient.Builder()
          .connectTimeout(10, TimeUnit.SECONDS)
          .readTimeout(10, TimeUnit.SECONDS)

      val interceptor = HttpLoggingInterceptor();
      interceptor.level = HttpLoggingInterceptor.Level.BODY;
//      httpClient.interceptors().add(interceptor)


      return Retrofit.Builder()
          .baseUrl(PRODUCTION_ENDPOINT)
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(MoshiConverterFactory.create(moshi))
          .client(httpClient.build())
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

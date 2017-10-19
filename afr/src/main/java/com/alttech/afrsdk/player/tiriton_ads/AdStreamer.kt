package com.alttech.afrsdk.player.tiriton_ads

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.tritondigital.ads.Ad
import com.tritondigital.ads.AdLoader
import com.tritondigital.ads.AdRequestBuilder
import java.util.concurrent.TimeUnit

/**
 * Created by bubu on 26/07/2017.
 */
object AdStreamer :
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener,
    AdLoader.AdLoaderListener {

  val THIRTY_MINS = TimeUnit.MINUTES.toMillis(30)

  private val adLoader = AdLoader()
  private var adRequestBuilder: AdRequestBuilder? = null
  var addInterface: AdInterface.CallBacks? = null
  private var params: Params? = null

  var adPlayer: MediaPlayer? = null
  var errMsg: String? = ""
  private var midRollAd = false
  private val adScheduleHandler = Handler()
  val timeTrackerHandler = Handler()

  var timeForLastAd: Long? = null

  private val futureAdRunnable = Runnable {
    midRollAd = true
    sourceAd()
    println("add sourcing restarted--------------")
  }

  private val timeTracker = object : Runnable {
    override fun run() {
      adPlayer?.let {
        if (it.isPlaying) {
          adPlayer?.let {
            addInterface?.progress(it.currentPosition, it.duration)
          }
        }
      }
      timeTrackerHandler.postDelayed(this, 1000)
    }
  }

  override fun onAdLoadingError(p0: AdLoader?, errorCode: Int) {
    addInterface?.adError("Ad loading FAILED: " + AdLoader.debugErrorToStr(errorCode))
    scheduleAdSourcing()
  }

  override fun onAdLoaded(p0: AdLoader?, ad: Bundle?) {

    if (ad != null)
      try {
        addInterface?.adLoaded(ad)
        adPlayer = MediaPlayer()
        adPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        adPlayer?.setOnCompletionListener(this@AdStreamer)
        adPlayer?.setOnErrorListener(this@AdStreamer)

        adPlayer?.setOnPreparedListener { mp ->
          addInterface?.adPlayStarted(midRollAd)
          mp?.start()
          Ad.trackImpression(ad);
          timeTrackerHandler.post(timeTracker)
        }

        adPlayer?.setDataSource(ad.getString(Ad.URL))
        adPlayer?.prepareAsync()
      } catch (e: Exception) {
        addInterface?.adError("Audio prepare exception: ${e.printStackTrace()}")
      }
    else {
      addInterface?.adError("Add is null")
    }
    timeForLastAd = System.currentTimeMillis()
    scheduleAdSourcing()
  }

  override fun onCompletion(p0: MediaPlayer?) {
    addInterface?.adPlayFinish(midRollAd)
    timeTrackerHandler.removeCallbacks(timeTracker)
  }

  override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
    errMsg = "Player error -> what -> $p1, where -> $p2"
    errMsg?.let { addInterface?.adError(it) }
    scheduleAdSourcing()
    return true
  }


  private fun scheduleAdSourcing() {
    var timeToNextAd = THIRTY_MINS

    timeForLastAd?.let { time ->
      timeToNextAd = time + THIRTY_MINS - System.currentTimeMillis()
    }
    if (timeToNextAd < 0) timeToNextAd = System.currentTimeMillis() + 6000

    adScheduleHandler.postDelayed(futureAdRunnable, timeToNextAd)
  }

  fun init(context: Context, params: Params) {
    AdStreamer.params = params
    if (adRequestBuilder == null)
      adRequestBuilder = AdRequestBuilder(context)
    if (adLoader.listener == null)
      adLoader.listener = this
  }

  fun timeUp(timeForLastAd: Long?): Boolean {
    if (timeForLastAd == null) return true

    // checking if time is past
    val timeLeft = timeForLastAd + THIRTY_MINS - System.currentTimeMillis()

    return timeLeft >= THIRTY_MINS

  }

  fun sourceAd() {
    if (params == null) throw RuntimeException("Parameters not initialized")


    // add has been played within the last 30 mins
    if (!timeUp(timeForLastAd)) {
      addInterface?.adPlayFinish(midRollAd)
      scheduleAdSourcing()
      return
    }


    adPlayer?.release()
    adPlayer = null

    addInterface?.adLoading()

    // Reset previous values
    params?.let {

      adRequestBuilder?.resetQueryParameters()

      adRequestBuilder
          ?.setHost(it.host)
          ?.addQueryParameter(AdRequestBuilder.STATION_ID, it.stationId)
          ?.addQueryParameter(AdRequestBuilder.ASSET_TYPE, AdRequestBuilder.ASSET_TYPE_VALUE_AUDIO)
          ?.addQueryParameter(AdRequestBuilder.TYPE,
              if (midRollAd) AdRequestBuilder.TYPE_VALUE_MIDROLL
              else AdRequestBuilder.TYPE_VALUE_PREROLL)
          ?.addQueryParameter(AdRequestBuilder.BANNERS, it.bannerSize)
          ?.addQueryParameter(AdRequestBuilder.COUNTRY_CODE, it.countryCode)

      it.yearOfBirth?.let { yob -> adRequestBuilder?.addQueryParameter(AdRequestBuilder.YEAR_OF_BIRTH, yob) }

      it.latitude?.let { adRequestBuilder?.addQueryParameter(AdRequestBuilder.LATITUDE, it) }
      it.longitude?.let { adRequestBuilder?.addQueryParameter(AdRequestBuilder.LONGITUDE, it) }

      it.gender?.let {
        adRequestBuilder?.addQueryParameter(AdRequestBuilder.GENDER,
            if (it == Params.GENDER.FEMALE) AdRequestBuilder.GENDER_VALUE_FEMALE
            else AdRequestBuilder.GENDER_VALUE_MALE)
      }


      params?.interests?.let { adRequestBuilder?.addTtags(it) }

      var adReq = adRequestBuilder?.build()

      val gaid = params?.gaid
      if (!TextUtils.isEmpty(gaid)) {
        val builtUri = Uri.parse(adReq)
        val lsid = builtUri.getQueryParameter("lsid")

        if (!lsid.contains("gaid") && gaid != null) {
          val params = builtUri.queryParameterNames
          val newUri = builtUri.buildUpon().clearQuery()
          for (s in params)
            if (s == "lsid")
              newUri.appendQueryParameter(s, "gaid:$gaid")
            else newUri.appendQueryParameter(s, builtUri.getQueryParameter(s))
          adReq = newUri.build().toString()
        }
      }

      adLoader.load(adReq)
    }
  }

  fun stopSourcing() {
    adScheduleHandler.removeCallbacks(futureAdRunnable)
  }


  fun onStop() {
    adScheduleHandler.removeCallbacks(futureAdRunnable)
    timeTrackerHandler.removeCallbacks(timeTracker)
    adLoader.cancel()
    adPlayer?.release()
  }

}

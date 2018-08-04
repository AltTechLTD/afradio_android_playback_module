package com.alttech.afrsdk.player

import android.content.Context
import android.graphics.Bitmap
import android.os.Parcelable
import com.alttech.afrsdk.data.Playback
import com.alttech.afrsdk.data.Show
import com.alttech.afrsdk.player.tiriton_ads.Params
import java.util.*
import kotlin.collections.ArrayList


object StreamManagerImpl : RadioListener {


  val listeners = ArrayList<RadioListener>()


  fun addOnPlayUpdateListener(listener: RadioListener.PlayUpdaterListener) {
    manager.addOnPlayUpdateListener(listener)
  }

  fun removeOnPlayUpdateListener(listener: RadioListener.PlayUpdaterListener) {
    manager.removeOnPlayUpdateListener(listener)
  }


  private val manager = OpenMxManager

  private var streamUrl: String? = null


  fun setup(context: Context) {
    manager.setLogging(true)
    manager.registerListener(this)
    manager.connect(context)

  }

  fun seek(i: Long) {
//    Analytics.get().seekInPlayback()
    manager.seek(i)
  }


  fun isPlaying() = manager.isPlaying


  private fun playStream(streamUrl: String?, showAds: Boolean) {
    streamUrl?.let {
      if (this.streamUrl != it) {
        this.streamUrl = it
        var params: Params? = null
//        if (showAds) params = SharedPrefsImpl.getInstance(context).getAdParams()
        manager.initStream(it, params)
      }
    }
  }

  fun playRequest(show: Show, playback: Playback) {

    playStream(playback.streamUrl, false)

    manager.updateNotification(show.name, "Playback", 0, null)

  }

  fun setNotification(bitmap: Bitmap) {
    manager.updateNotificationBitmap(bitmap)
  }


  fun getCountryByCode(key: String): String {
    val loc = Locale("", key)
    return loc.displayCountry
  }

  fun play() = manager.play()
  fun pause() = manager.pause()


  override fun playingAd() {
    listeners.forEach { it.playingAd() }
  }

  override fun metaData(meta: Parcelable) {
    listeners.forEach { it.metaData(meta) }
  }

  fun setRadiolistener(listener: RadioListener) {
    listeners.add(listener)
  }

  fun removelistener(listener: RadioListener) {
    listeners.remove(listener)
  }


  override fun onRadioLoading() {
    listeners.forEach { it.onRadioLoading() }
  }

  override fun onRadioConnected() {
    listeners.forEach { it.onRadioConnected() }
  }

  override fun onRadioStarted(mime: String, sampleRate: Int, channels: Int, duration: Long) {
    listeners.forEach { it.onRadioStarted(mime, sampleRate, channels, duration) }
  }

  override fun onRadioPlay() {
    listeners.forEach { it.onRadioPlay() }
  }

  override fun onRadioPaused() {

    listeners.forEach { it.onRadioPaused() }
  }

  override fun onRadioSwitching() {
    listeners.forEach { it.onRadioSwitching() }
  }

  override fun onRadioStopped() {
    listeners.forEach { it.onRadioStopped() }
  }


  override fun onError() {
    listeners.forEach { it.onError() }
  }
}

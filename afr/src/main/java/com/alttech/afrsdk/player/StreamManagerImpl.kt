package com.alttech.afrsdk.player

import android.content.Context
import android.graphics.Bitmap
import android.os.Parcelable
import com.alttech.afrsdk.data.Playback
import com.alttech.afrsdk.data.Show
import com.alttech.afrsdk.player.tiriton_ads.Params
import java.util.*


object StreamManagerImpl : RadioListener {


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
//    RxBus.post(Events.RadioListener(Events.RadioListener.ListenerEvents.PLAYING_AD))
  }

  override fun metaData(meta: Parcelable) {
//    RxBus.post(Events.AdEvent(meta as Bundle))
  }


  fun onPlay() {
  }

  override fun onRadioLoading() {
  }

  override fun onRadioConnected() {
  }

  override fun onRadioStarted(mime: String, sampleRate: Int, channels: Int, duration: Long) {
    radioStarted()
  }

  internal fun radioStarted() {
  }

  override fun onRadioPlay() {
    radioStarted()
  }

  override fun onRadioPaused() {
  }

  override fun onRadioSwitching() {
  }

  override fun onRadioStopped() {
  }


  override fun onError() {
  }
}

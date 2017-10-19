package com.alttech.afrsdk.player.openmxplayer

import android.os.Parcelable

/**
 * Created by bubu on 12/15/16.
 */


interface PlayerEvents {
  fun onStart(mime: String, sampleRate: Int, channels: Int, duration: Long)

  fun onPlay()

  fun onPause()

  fun updatePlay(percent: Int, currentms: Long, totalms: Long)

  fun onLoad()

  fun onSwitch()

  fun onStop()

  fun playinAd()

  fun meta(meta: Parcelable)

  fun onError(t: Throwable)
}

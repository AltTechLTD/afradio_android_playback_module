package com.alttech.afrsdk.player

import android.os.Parcelable

/**
 * Created by mertsimsek on 01/07/15.
 */
interface RadioListener {

  fun onRadioLoading()

  fun onRadioSwitching()

  fun onRadioConnected()

  fun onRadioStarted(mime: String, sampleRate: Int, channels: Int, duration: Long)

  fun onRadioPlay()

  fun onRadioPaused()

  fun onRadioStopped()

  fun playingAd()

  fun onError()

  fun metaData(meta: Parcelable)

  interface PlayUpdaterListener {
    fun update(progress: Float, current: Long, total: Long)
  }
}

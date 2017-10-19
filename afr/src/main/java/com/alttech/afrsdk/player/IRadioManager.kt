package com.alttech.afrsdk.player


import android.graphics.Bitmap

/**
 * Created by mertsimsek on 03/07/15.
 */
internal interface IRadioManager {

  fun play()

  fun stop()

  fun pause()

  val isPlaying: Boolean

  val isLive: Boolean

  fun seek(progress: Long)

  fun registerListener(mRadioListener: RadioListener)

  fun unregisterListener(mRadioListener: RadioListener)

  fun setLogging(logging: Boolean)

  fun connect()

  fun disconnect()

  fun updateNotification(singerName: String?, songName: String?, smallArt: Int, bigArt: Int)

  fun updateNotification(singerName: String?, songName: String?, smallArt: Int, bigArt: Bitmap?)

}

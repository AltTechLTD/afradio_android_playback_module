package com.alttech.afrsdk.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import co.mobiwise.library.radio.RadioPlayerService
import com.alttech.afrsdk.player.tiriton_ads.Params

/**
 * Created by mertsimsek on 03/07/15.
 */
object OpenMxManager : IRadioManager {


  private var playClass: Class<out AppCompatActivity>? = null

  private val mRadioListenerQueue = ArrayList<RadioListener>()
  private val playbackListenerQueue = ArrayList<RadioListener.PlayUpdaterListener>()

  private var isServiceConnected = false

  private val mServiceConnection = object : ServiceConnection {

    override fun onServiceConnected(arg0: ComponentName, binder: IBinder) {

      log("Service Connected.")

      service = (binder as RadioPlayerService.LocalBinder).service
      service?.setLogging(isLogging)
      playClass?.let { service?.setPlayActivity(it) }

      isServiceConnected = true
      if (!mRadioListenerQueue.isEmpty()) {
        for (mRadioListener in mRadioListenerQueue) {
          registerListener(mRadioListener)
          mRadioListener.onRadioConnected()
        }
      }

      if (!playbackListenerQueue.isEmpty()) {
        for (mRadioListener in playbackListenerQueue) {
          addOnPlayUpdateListener(mRadioListener)
        }
      }
    }

    override fun onServiceDisconnected(arg0: ComponentName) {}
  }

  override fun play() {
    service?.play()
  }

  override fun stop() {
    service?.stop()
  }

  override fun pause() {
    service?.pause()
  }

  override val isPlaying: Boolean
    get() {
      service?.let {
        log("IsPlaying : " + service?.isPlaying)
        return it.isPlaying
      }
      return false
    }

  override fun seek(progress: Long) {
    service?.seek(progress)
  }

  override fun registerListener(mRadioListener: RadioListener) {
    if (isServiceConnected)
      service?.registerListener(mRadioListener)
    else
      mRadioListenerQueue.add(mRadioListener)
  }


  fun addOnPlayUpdateListener(listener: RadioListener.PlayUpdaterListener) {
    if (isServiceConnected)
      service?.addOnPlayUpdateListener(listener)
    else
      playbackListenerQueue.add(listener)
  }

  fun removeOnPlayUpdateListener(listener: RadioListener.PlayUpdaterListener) {
    if (service != null)
      service?.removeOnPlayUpdateListener(listener)
  }

  override fun unregisterListener(mRadioListener: RadioListener) {
    log("Register unregistered.")
    service?.unregisterListener(mRadioListener)
  }

  override fun setLogging(logging: Boolean) {
    isLogging = logging
  }

  override fun connect(context: Context) {
    log("Requested to connect service.")
    val intent = Intent(context, RadioPlayerService::class.java)
    context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
  }

  override fun disconnect(context: Context) {
    log("Service Disconnected.")
    context.unbindService(mServiceConnection)
  }


  override fun updateNotification(singerName: String?, songName: String?, smallArt: Int, bigArt: Int) {
    service?.updateNotification(singerName, songName, smallArt, bigArt)
  }

  override fun updateNotification(singerName: String?, songName: String?, smallArt: Int, bigArt: Bitmap?) {
    service?.updateNotification(singerName, songName, smallArt, bigArt)
  }

  fun updateNotificationBitmap(bitmap: Bitmap) {
    service?.updateNotificationBitmap(bitmap)
  }


  fun startActivity(activityClass: Class<out AppCompatActivity>) {
    playClass = activityClass
  }

  fun initStream(url: String, params: Params?) {
    if (!TextUtils.isEmpty(url))
      service?.initStream(url, params)
  }

  private fun log(log: String) {
    if (isLogging)
      Log.v("RadioManager", "RadioManagerLog : " + log)
  }

  fun progress(timeLeft: Long, totalTime: Long) {
    if (service != null)
      service?.progress(timeLeft, totalTime)
  }

  override val isLive: Boolean
    get() = service?.isLive as Boolean

  private var isLogging = false


  var service: RadioPlayerService? = null
    private set
}

package com.alttech.afrsdk.player

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.alttech.afrsdk.player.openmxplayer.MediaPlayerApi
import com.alttech.afrsdk.player.openmxplayer.OpenMXPlayerInterface
import com.alttech.afrsdk.player.openmxplayer.PlayerEvents
import com.alttech.afrsdk.player.openmxplayer.PlayerStates
import com.alttech.afrsdk.player.tiriton_ads.Params
import java.util.*
import java.util.concurrent.TimeUnit


class RadioPlayerService : Service(), PlayerEvents {
  override fun onStart(mime: String, sampleRate: Int, channels: Int, duration: Long) {
  }

  internal var playActivity: Class<out AppCompatActivity>? = null

  internal var mWifiLock: WifiManager.WifiLock? = null
  val mLocalBinder: IBinder = LocalBinder()
  internal var mListenerList: MutableList<RadioListener>? = null
  internal var playUpdateListeners: MutableList<RadioListener.PlayUpdaterListener>? = null

  private var stationName = ""
  private var showName = ""
  private var smallImage = com.alttech.afrsdk.R.drawable.ic_stat_name
  private var artImage: Bitmap? = null
  private var playingAd = false

  private val mRadioState: PlayerStates = PlayerStates(this)

  internal var notificationCompatBuilder: NotificationCompat.Builder? = null
  internal var notificationManager: NotificationManager? = null

  private var startedForeground = false

  private var countDownTimer: CountDownTimer? = null


  private var player: OpenMXPlayerInterface? = null

  /**
   * While current radio playing, if you give another play command with different
   * source, you need to stop it first. This value is responsible for control
   * after radio stopped.
   */
  //    private boolean isSwitching;

  /**
   * If closed from notification, it will be checked
   * on Stop method and notification will not be created
   */

  /**
   * Incoming calls interrupt radio if it is playing.
   * Check if this is true or not after hang up;
   */
  private var isInterrupted: Boolean = false

  internal var phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
    override fun onCallStateChanged(state: Int, incomingNumber: String) {
      if (state == TelephonyManager.CALL_STATE_RINGING) {
        if (isPlaying) {
          isInterrupted = true
          pause()
        }
      } else if (state == TelephonyManager.CALL_STATE_IDLE) {
        if (isInterrupted)
          play()
      } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
        if (isPlaying) {
          isInterrupted = true
          pause()
        }

      }
      super.onCallStateChanged(state, incomingNumber)
    }
  }

  override fun onBind(intent: Intent) = mLocalBinder

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

    log("command is received here")

    intent?.let {
      val action = it.action
      when (action) {
        NOTIFICATION_INTENT_CANCEL -> stop()
        NOTIFICATION_INTENT_PLAY_PAUSE -> if (isPlaying) pause() else play()
        NOTIFICATION_INTENT_OPEN_PLAYER -> if (playActivity != null) {
          val i = Intent(this, playActivity)
          i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
          startActivity(i)
        }
      }
    }

    return Service.START_NOT_STICKY
  }

  internal fun relaxResources() {
    mWifiLock?.let { if (it.isHeld) it.release() }
    countDownTimer?.cancel()
    player = null
  }

  override fun onCreate() {
    super.onCreate()

    mListenerList = ArrayList<RadioListener>()
    playUpdateListeners = ArrayList<RadioListener.PlayUpdaterListener>()

    isInterrupted = false

    val mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    mWifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
        .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock")

  }


  fun play() {

    if (isPlaying) {
      log("Switching Radio")
      pause()
    } else {
      if (player != null) {
        log("Play requested.")
        mWifiLock?.acquire()
        player?.play()
      }
    }
  }

  val isLive: Boolean
    get() = player?.isLive() as Boolean

  fun pause() {
    player?.pause()
  }

  fun stop() {
//    println("=============================== stop command in service. state -> ${mRadioState.state}")
//    if (!(mRadioState.isStopped || mRadioState.isPaused)) {
//      log("Stop requested.")
//      player?.stop()
//    }

    if (player != null) {
      player?.stop()
    } else {
      onStop()
    }

    stopForeground(true)
  }

  val isPlaying: Boolean
    get() = mRadioState.isPlaying

  fun seek(pos: Long) {
    player?.seek(pos)
  }

  val isLoading: Boolean
    get() = mRadioState.isLoading


  fun registerListener(mListener: RadioListener) {
    mListenerList?.add(mListener)
  }

  fun unregisterListener(mListener: RadioListener) {
    mListenerList?.remove(mListener)
  }


  private fun initPlayer() {

    player?.stop()

    player = MediaPlayerApi.init(mRadioState)
  }

  fun setLogging(logging: Boolean) {
    isLogging = logging
  }

  private fun log(log: String) {
    if (isLogging)
      Log.v("RadioManager", "RadioPlayerService : " + log)
  }

  private fun buildNotification() {

    log("building notification")

    val intentPlayPause = Intent(NOTIFICATION_INTENT_PLAY_PAUSE)
    val intentCancel = Intent(NOTIFICATION_INTENT_CANCEL)
    val intentOpenPlayer = Intent(NOTIFICATION_INTENT_OPEN_PLAYER)

    val playPausePending = PendingIntent.getService(this, 0, intentPlayPause, PendingIntent.FLAG_CANCEL_CURRENT)
    val cancelPending = PendingIntent.getService(this, 0, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT)
    val openPending = PendingIntent.getService(this, 0, intentOpenPlayer, PendingIntent.FLAG_CANCEL_CURRENT)


    if (artImage == null)
      artImage = BitmapFactory.decodeResource(resources,  com.alttech.afrsdk.R.drawable.no_cover)

    smallImage = com.alttech.afrsdk.R.drawable.ic_stat_name

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannel(this)
    }

    notificationCompatBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

    notificationCompatBuilder
        ?.setColor(ContextCompat.getColor(this, com.alttech.afrsdk.R.color.red))
        ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        ?.setOnlyAlertOnce(true)
        ?.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
            this, PlaybackStateCompat.ACTION_STOP))
        ?.setLargeIcon(artImage)
        ?.setSmallIcon(smallImage)
        ?.setContentTitle(stationName)
        ?.setContentText(showName + " - " + mRadioState.state)
        ?.setContentIntent(openPending)
        ?.setDeleteIntent(cancelPending)
        ?.setOngoing(false)


    if (!playingAd)
      notificationCompatBuilder
          ?.setAutoCancel(!isPlaying)
          ?.addAction(com.alttech.afrsdk.R.drawable.ic_stop_black_24dp, resources.getString(com.alttech.afrsdk.R.string.stop), cancelPending)
          ?.addAction(
              if (isPlaying) com.alttech.afrsdk.R.drawable.ic_pause_black_24dp else com.alttech.afrsdk.R.drawable.ic_play_arrow_black_24dp,
              if (isPlaying) resources.getString(com.alttech.afrsdk.R.string.pause) else resources.getString(com.alttech.afrsdk.R.string.play), playPausePending)
          ?.setStyle(
              MediaStyle()
//                  .setMediaSession("")
                  .setShowCancelButton(true)
                  .setCancelButtonIntent(
                      MediaButtonReceiver.buildMediaButtonPendingIntent(
                          this, PlaybackStateCompat.ACTION_STOP)))

    notificationCompatBuilder?.setSubText("AF Radio")

    notificationCompatBuilder?.let {
      applyLollipopFunctionality(it)
    }

    if (startedForeground) {
      notificationManager?.notify(NOTIFICATION_ID, notificationCompatBuilder?.build())
    } else {
      val intent = Intent(this, RadioPlayerService::class.java)
      ContextCompat.startForegroundService(this, intent)
      startForeground(NOTIFICATION_ID, notificationCompatBuilder?.build())
      startedForeground = true
    }
  }


  private fun applyLollipopFunctionality(notificationCompatBuilder: NotificationCompat.Builder) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      notificationCompatBuilder
          .setCategory(Notification.CATEGORY_TRANSPORT)
          .setVisibility(Notification.VISIBILITY_PUBLIC)
    }
  }

  fun updateNotification(stationName: String?, showName: String?, smallImage: Int, artImage: Int) {
    stationName?.let { this.stationName = it }
    showName?.let { this.showName = it }
    this.smallImage = smallImage
    this.artImage = BitmapFactory.decodeResource(resources, artImage)
    buildNotification()
  }

  fun updateNotification(singerName: String?, stationName: String?, smallImage: Int, artImage: Bitmap?) {
    singerName?.let { this.showName = it }
    stationName?.let { this.stationName = it }
    this.smallImage = smallImage
    this.artImage = artImage
    buildNotification()
  }

  fun updateNotificationBitmap(artImage: Bitmap?) {
    this.artImage = artImage
    buildNotification()
  }


  fun setPlayActivity(playActivity: Class<out AppCompatActivity>) {
    this.playActivity = playActivity
  }

  fun initStream(streamUrl: String, params: Params?) {
    initPlayer()

    params?.let {
      player?.showAds(this, it)
    }

    player?.setDataSource(streamUrl)
  }

  fun progress(timeLeft: Long, totalTime: Long) {
    if (timeLeft > 0)
      countDownTimer = object : CountDownTimer(timeLeft, TimeUnit.SECONDS.toMillis(1)) {

        override fun onTick(millisUntilFinished: Long) {
          val percent = (totalTime - millisUntilFinished) * 100.0f / totalTime
          onPlayUpdated(percent, totalTime - millisUntilFinished, totalTime)
        }

        override fun onFinish() {}

      }.start()

  }

  fun onPlayUpdated(percent: Float, currentTime: Long, totalTime: Long) {
    playUpdateListeners?.let {
      for (p in it)
        p.update(percent, currentTime, totalTime)
    }
  }

  fun addOnPlayUpdateListener(playUpdaterListener: RadioListener.PlayUpdaterListener) {
    playUpdateListeners?.add(playUpdaterListener)
  }

  fun removeOnPlayUpdateListener(playUpdaterListener: RadioListener.PlayUpdaterListener) {
    playUpdateListeners?.remove(playUpdaterListener)
  }

//  fun updateNotification(stationName: String?, showName: String?, smallImage: Int, artImage: Int) {
//    stationName?.let { this.stationName = it }
//    showName?.let { this.showName = it }
//    override fun onStart(mime: String, sampleRate: Int, channels: Int, duration: Long) {
//      mListenerList?.let {
//        for (mRadioListener in it) {
//          mRadioListener.onRadioStarted(mime, sampleRate, channels, duration)
//        }
//      }
//  }

  override fun onPlay() {
    playingAd = true
    if (isInterrupted)
      isInterrupted = false

    mListenerList?.let {
      for (mRadioListener in it) {
        mRadioListener.onRadioPlay()
      }
    }
    buildNotification()
  }

  override fun updatePlay(percent: Int, currentms: Long, totalms: Long) {
    onPlayUpdated(percent.toFloat(), currentms, totalms)
  }


  override fun onLoad() {
    mListenerList?.let {
      for (mRadioListener in it)
        mRadioListener.onRadioLoading()
    }
    buildNotification()
  }

  override fun onSwitch() {
    onLoad()
    mListenerList?.let {
      for (mRadioListener in it)
        mRadioListener.onRadioSwitching()
    }
  }

  override fun onPause() {
    mListenerList?.let {
      for (mRadioListener in it) {
        mRadioListener.onRadioPaused()
      }
    }
    buildNotification()
  }

  override fun onStop() {
    relaxResources()
    notificationManager?.cancel(NOTIFICATION_ID)
    mListenerList?.let {
      for (mRadioListener in it) {
        mRadioListener.onRadioStopped()
      }
    }
  }

  override fun playinAd() {
    playingAd = true
    mListenerList?.let {
      for (mRadioListener in it) {
        mRadioListener.playingAd()
      }
    }
    buildNotification()

  }

  override fun meta(meta: Parcelable) {
    mListenerList?.let {
      for (mRadioListener in it) {
        mRadioListener.metaData(meta)
      }
    }
  }

  override fun onError(t: Throwable) {
    t.printStackTrace()
    mListenerList?.let {
      for (mRadioListener in it) {
        mRadioListener.onError()
      }
    }
    buildNotification()
  }

  override fun onDestroy() {
    stop()
    super.onDestroy()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createChannel(context: Context) {
    val mNotificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    // The id of the channel.
    val id = NOTIFICATION_CHANNEL_ID
    // The user-visible name of the channel.
    val name = "Media playback"
    // The user-visible description of the channel.
    val description = "Media playback controls"
    val importance = NotificationManager.IMPORTANCE_LOW
    val mChannel = NotificationChannel(id, name, importance)
    mChannel.description = description
    mChannel.setShowBadge(false)
    mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    mNotificationManager.createNotificationChannel(mChannel)
  }

  /**
   * Binder
   */
  internal inner class LocalBinder : Binder() {
    val service: RadioPlayerService
      get() = this@RadioPlayerService
  }

  companion object {

    private const val NOTIFICATION_INTENT_PLAY_PAUSE = "xyz.alttech.afr.widget.INTENT_PLAYPAUSE"
    private const val NOTIFICATION_INTENT_CANCEL = "xyz.alttech.afr.widget.INTENT_CANCEL"
    const val NOTIFICATION_INTENT_OPEN_PLAYER = "xyz.alttech.afr.widget.INTENT_OPENPLAYER"
    private const val NOTIFICATION_ID = 4924
    private const val NOTIFICATION_CHANNEL_ID = "alttech.Player.Notification"
    private var isLogging = false
  }
}

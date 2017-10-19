package com.alttech.afrsdk.player.openmxplayer

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import com.alttech.afrsdk.player.tiriton_ads.AdInterface
import com.alttech.afrsdk.player.tiriton_ads.AdStreamer
import com.alttech.afrsdk.player.tiriton_ads.Params
import java.io.IOException

/**
 * Created by bubu on 12/05/2017.
 */

object MediaPlayerApi :
    OpenMXPlayerInterface,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnSeekCompleteListener,
    AdInterface.CallBacks {


  var adLoadSuccess = true
  var shouldLoadAd = false
  var playbackStreamReady = false
  var adFinished = false

  private var isSwitching: Boolean = false

  private var mRadioUrl: String? = null

  private var player: MediaPlayer? = null

  private lateinit var states: PlayerStates

  init {
    isSwitching = false
  }

  fun init(states: PlayerStates): MediaPlayerApi {
    MediaPlayerApi.states = states
    return this
  }


  override fun showAds(context: Context, params: Params) {
    shouldLoadAd = true
    AdStreamer.init(context, params)
    AdStreamer.addInterface = this
  }


  override fun isLive() = false

  override fun setDataSource(url: String) {


    if (shouldLoadAd)
      AdStreamer.sourceAd()

    if (TextUtils.equals(url, mRadioUrl))
      isSwitching = true

    mRadioUrl = url
    play()
  }

  override fun play() {
    if (player != null)
      if (isSwitching || states.isPaused) {
        states.set(PlayerStates.State.PLAYING)
        player?.start()
        return
      }

    if (player != null || states.errorOccurred())
      player?.reset()
    else
      player = MediaPlayer()


    player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
    try {
      player?.setOnBufferingUpdateListener(this)
      player?.setOnCompletionListener(this)
      player?.setOnErrorListener(this)
      player?.setOnPreparedListener(this)
      player?.setOnSeekCompleteListener(this)
      player?.setDataSource(mRadioUrl)
      player?.prepareAsync()
      states.set(PlayerStates.State.LOADING)


    } catch (e: IOException) {
      e.printStackTrace()
    }

  }

  override fun stop() {
    if (states.isPlaying || states.isPaused) {
      player?.stop()
      states.set(PlayerStates.State.STOPPED)
      player?.release()
      player = null
      AdStreamer.stopSourcing()
      AdStreamer.onStop()
    }

  }

  override fun pause() {
    if (states.isPlaying) {
      player?.pause()
      states.set(PlayerStates.State.PAUSED)
      AdStreamer.stopSourcing()
    }
  }

  override fun seek(pos: Long) {
    if (states.isPlaying || states.isPaused || states.isSeeking) {
      player?.seekTo(pos.toInt())
      states.set(PlayerStates.State.SEEKING)
    }
  }

  override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
    states.setError(Throwable("[[Media player Error]] [$what] [$extra]"))
    return false
  }

  override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
    if (mp.isPlaying)
      states.update(percent, mp.currentPosition.toLong(), mp.duration.toLong())
  }

  override fun onCompletion(mp: MediaPlayer) {
    stop()
  }

  override fun onPrepared(mp: MediaPlayer) {
    playbackStreamReady = true
    if (shouldLoadAd) {
      if (adFinished) {
        mp.start()
        states.set(PlayerStates.State.PLAYING)
      }
      adFinished = false
    } else {
      mp.start()
      states.set(PlayerStates.State.PLAYING)
    }
  }

  override fun onSeekComplete(mp: MediaPlayer) {
    states.set(PlayerStates.State.PLAYING)
  }


  override fun adLoading() {
    println("[[Triton Audio Ads] - AD PLAY LOADING ]")
  }

  override fun adLoaded(ad: Bundle) {
    println("[[Triton Audio Ads] - AD PLAY LOADED ]")
    adLoadSuccess = true
    states.meta(ad)
  }

  override fun adPlayStarted(midRollAd: Boolean) {
    println("[[Triton Audio Ads] - AD PLAY STARTED ] midroll? $midRollAd")
    if (midRollAd || player?.isPlaying as Boolean)
      player?.pause()
    states.set(PlayerStates.State.PLAYING_AD)
  }

  override fun adPlayFinish(midRollAd: Boolean) {
    println("[[Triton Audio Ads] - AD PLAY FINISH ] midroll? $midRollAd")
    adFinished = true
    if (playbackStreamReady || midRollAd)
      player?.let { onPrepared(it) }
  }

  override fun adError(message: String) {
    adFinished = true
    if (playbackStreamReady)
      player?.let { onPrepared(it) }
    println("[[Triton Audio Ads] - ERROR ] $message")
  }

  override fun progress(current: Int, total: Int) {
    states.update((current / total) * 100, current.toLong(), total.toLong())
  }
}

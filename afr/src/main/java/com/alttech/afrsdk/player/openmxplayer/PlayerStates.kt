package com.alttech.afrsdk.player.openmxplayer

import android.os.Handler
import android.os.Parcelable

/**
 * Created by bubu on 12/15/16.
 */

class PlayerStates(private val events: PlayerEvents) {
  /**
   * Playing state which can either be stopped, playing, or reading the header before playing
   */

  internal enum class State {
    PLAYING,
    PAUSED,
    STOPPED,
    SWITCHING,
    LOADING,
    SEEKING,
    ERROR,
    PLAYING_AD
  }

  private val handler = Handler()

  private var playerState = State.STOPPED

  internal fun set(state: State) {
    playerState = state
    handler.post {
      when (playerState) {
        State.PLAYING -> events.onPlay()
        State.PAUSED -> events.onPause()
        State.STOPPED -> events.onStop()
        State.LOADING, State.SEEKING -> events.onLoad()
        State.SWITCHING -> events.onSwitch()
        State.PLAYING_AD -> events.playinAd()
        else -> {
        }
      }
    }
  }

  internal fun setError(t: Throwable) {
    playerState = State.ERROR

    handler.post { events.onError(t) }
  }

  internal fun update(progress: Int, current: Long, total: Long) {
    handler.post { events.updatePlay(progress, current, total) }
  }

  internal fun meta(meta: Parcelable) {
    handler.post { events.meta(meta) }
  }

  val isPaused: Boolean
    get() = playerState == State.PAUSED

  val state: String
    get() {
      when (playerState) {
        State.PLAYING -> return "Playing"
        State.LOADING, State.SEEKING -> return "Loading"
        State.PAUSED -> return "Paused"
        State.SWITCHING -> return "Switching"
        else -> return "Stopped"
      }
    }


  val isPlaying: Boolean
    get() = playerState == State.PLAYING

  val isLoading: Boolean
    get() = playerState == State.LOADING
  val isSeeking: Boolean
    get() = playerState == State.SEEKING

  val isStopped: Boolean
    get() = playerState == State.STOPPED

  fun errorOccurred(): Boolean {
    return playerState == State.ERROR
  }

  val isSwitching: Boolean
    get() = playerState == State.SWITCHING

}

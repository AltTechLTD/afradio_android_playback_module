package com.alttech.afrsdk.player.tiriton_ads

import android.os.Bundle

/**
 * Created by bubu on 26/07/2017.
 */
class AdInterface {

  interface CallBacks {
    fun adLoading()
    fun adLoaded(ad: Bundle)
    fun adPlayStarted(midRollAd: Boolean)
    fun adPlayFinish(midRollAd: Boolean)
    fun adError(message: String)
    fun progress(current: Int, total: Int)
  }
}

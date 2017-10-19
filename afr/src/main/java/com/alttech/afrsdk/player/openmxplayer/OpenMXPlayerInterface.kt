package com.alttech.afrsdk.player.openmxplayer

import android.content.Context
import com.alttech.afrsdk.player.tiriton_ads.Params

/**
 * Created by bubu on 4/6/17.
 */

interface OpenMXPlayerInterface {

  fun isLive(): Boolean

  fun setDataSource(url: String)

  fun showAds(context: Context, params: Params)

  fun play()

  fun stop()

  fun pause()

  fun seek(pos: Long)
}

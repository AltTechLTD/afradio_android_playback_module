package com.alttech.afrsdk.views

import com.alttech.afrsdk.Config
import com.alttech.afrsdk.data.*
import com.alttech.afrsdk.network.ApiCalls
import rx.subscriptions.CompositeSubscription

/**
 * Created by bubu on 26/10/2017.
 */

class PlaybackPresenter(private val config: Config) {

  var view: PlaybackView? = null

  var playbackListPosition = 0

  val cs = CompositeSubscription()

  fun subscribe(view: PlaybackView) {
    this.view = view
  }

  fun unSubscribe() {
    cs.clear()
    this.view = null
  }


  fun fetchWidgetData() {
    cs.add(ApiCalls.getShows(config.appId!!, config.resId!!)
        .doOnSubscribe { view?.progressView(false) }
        .subscribe({ t ->
          view?.progressView(false)
          view?.showWidgetData(t)
        }, {
          view?.progressView(false)
          view?.loadDataError()
          it.printStackTrace()
        })
    )
  }

  fun getPlaybackList(show: Show, position: Int) {
    if (playbackListPosition > 0) view?.removeItem(playbackListPosition)

    val col = if (position > playbackListPosition) position - 1 else position

    val p = position - if (playbackListPosition < position) 1 else 0
    playbackListPosition = p + (view!!.getColumnSize() - (p % view!!.getColumnSize()))
    view?.addPlaybackList(playbackListPosition, PlaybackList(col % view!!.getColumnSize(), show, ArrayList(show.playback), 0, show.playback!!.size))
  }

  fun loadMore(showId: String?, offset: Int, limit: Int) {
    cs.add(ApiCalls.loadMore(config.appId!!, showId!!, offset, limit)
        .subscribe({
          view?.progressView(false)
          view?.showMoreData(showId, it)
        }, {
          view?.progressView(false)
          view?.loadDataError()
          it.printStackTrace()
        })
    )
  }

  fun play(playback: Playback){
    

  }

  interface PlaybackView {
    fun showWidgetData(data: WidgetDataResult)
    fun showMoreData(showId: String, data: LoadMoreDataResult)
    fun loadDataError()
    fun removeItem(pos: Int)
    fun addPlaybackList(pos: Int, playbackList: PlaybackList)
    fun getColumnSize(): Int
    fun progressView(show: Boolean)
    fun showErrorText(txt: String)
    fun getPlaybackPos(): Int
  }
}
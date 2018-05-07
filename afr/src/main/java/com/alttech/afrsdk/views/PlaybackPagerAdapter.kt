package com.alttech.afrsdk.views

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.alttech.afrsdk.DividerItemDecoration
import com.alttech.afrsdk.R
import com.alttech.afrsdk.data.LoadMoreDataResult
import com.alttech.afrsdk.data.PlaybackList

/**
 * Created by bubu on 17/03/2018.
 */

class PlaybackPagerAdapter(private val callback: ShowsAdapter.ShowAdapterInterface) : PagerAdapter() {

  private val views = ArrayList<View>()

  private val limit = 7

  private var arrowPos = 0
  private var offset = 0

  //
  init {
    callback.addMoreDataListener(object : ShowsAdapter.OnMoreData {
      override fun showMoreData(showId: String, data: LoadMoreDataResult) {
        if (views.size > 0) {
          val v = getView(views.size - 1)
          addPlaybackData(v.context, PlaybackList(arrowPos, showId, ArrayList(data.playbacks), data.offsett, data.count))
        }
      }
    })
  }
//

  override fun getCount(): Int = views.size

  override fun getItemPosition(`object`: Any): Int {
    val index = views.indexOf(`object`)
    return if (index == -1)
      POSITION_NONE
    else
      index

//    return POSITION_NONE
  }

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val v = views.get(position)
    container.addView(v)
    return v
  }

  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    container.removeView(views.get(position))
  }

  override fun isViewFromObject(view: View, `object`: Any) = view === `object`

  fun addView(v: View, position: Int): Int {
    views.add(position, v)
    return position
  }

  fun removeView(pager: ViewPager, position: Int): Int {
    pager.adapter = null
    views.removeAt(position)
    pager.adapter = this
    return position
  }

  fun getView(position: Int) = views.get(position)


  fun addPlaybackData(context: Context?, playbackList: PlaybackList) {

    arrowPos = playbackList.itemColumn


    val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    if (playbackList.offsett == 0) {
      views.clear()
      val lm = inflater.inflate(R.layout.fragment_playback_loadmore, null) as FrameLayout
      lm.findViewById<Button>(R.id.load_more).setOnClickListener {
        callback.loadMore(playbackList.showId, playbackList.offsett + playbackList.count, limit)
      }

      this.addView(lm, 0)

    }


    val view = inflater.inflate(R.layout.fragment_playback_view_pager_list, null) as FrameLayout
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
    val adapter = PlaybackItemAdapter(playbackList.playbacks, callback)
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter


    recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context))


    this.addView(view, views.size - 1)

//    if (playbackList.count != limit) views.removeAt(views.size - 1)


    this.notifyDataSetChanged()


  }


  override fun getPageWidth(position: Int) = 0.93f

}

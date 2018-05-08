package com.alttech.afrsdk.views

import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.alttech.afrsdk.R
import com.alttech.afrsdk.data.LoadMoreDataResult
import com.alttech.afrsdk.data.Playback
import com.alttech.afrsdk.data.PlaybackList
import com.alttech.afrsdk.data.Show
import com.alttech.afrsdk.loadUrl
import me.relex.circleindicator.CircleIndicator
import java.io.Serializable

/**
 * Created by bubu on 19/10/2017.
 */

class ShowsAdapter(val data: List<*>, val adapterInterface: ShowAdapterInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val SHOW_TYPE = 1
  private val SHOW_PLAYBACKS = 2

  private val pagerAdapter = PlaybackPagerAdapter(adapterInterface)

  override fun getItemViewType(position: Int): Int {
    return when (data[position]) {
      is Show -> SHOW_TYPE
      is PlaybackList -> SHOW_PLAYBACKS
      else -> 0
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      SHOW_TYPE -> ShowHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_shows, parent, false))
      else -> PlaybackHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_shows_playbacks, parent, false))
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ShowHolder -> {
        val show = data[position] as Show
        show.imgUrl?.let { holder.img?.loadUrl(it) }
        show.name?.let { holder.showName?.text = it }
      }

      is PlaybackHolder -> {
        val playbackList = data[position] as PlaybackList
        holder.columnList.forEachIndexed { index, relativeLayout ->
          if (index == playbackList.itemColumn)
            relativeLayout?.visibility = View.VISIBLE
          else
            relativeLayout?.visibility = View.INVISIBLE
        }


        holder.viewPager?.adapter = pagerAdapter


        pagerAdapter.addPlaybackData(holder.viewPager?.context,  playbackList){
          holder.viewPager?.currentItem = it
        }

        holder.viewPager?.clipToPadding = false
        holder.viewPager?.pageMargin = 12
        holder.indicator?.setViewPager(holder.viewPager)

        pagerAdapter.registerDataSetObserver(holder.indicator!!.getDataSetObserver());

      }
    }

  }


  override fun getItemCount() = data.size

  inner class ShowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var img: ImageView? = null
    var showName: TextView? = null


    init {
      img = itemView.findViewById(R.id.show_img)
      showName = itemView.findViewById(R.id.show_name)
      itemView.setOnClickListener {
        adapterInterface.expandPlayback(data[adapterPosition] as Show, adapterPosition)
      }
    }

  }


  inner class PlaybackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var column1: RelativeLayout? = null
    private var column2: RelativeLayout? = null
    private var column3: RelativeLayout? = null

    var columnList: ArrayList<RelativeLayout?>

    var viewPager: ViewPager? = null
    var indicator: CircleIndicator? = null

    init {
      column1 = itemView.findViewById(R.id.column_1)
      column2 = itemView.findViewById(R.id.column_2)
      column3 = itemView.findViewById(R.id.column_3)
      columnList = arrayListOf(column1, column2, column3)

      viewPager = itemView.findViewById(R.id.view_pager)
      indicator = itemView.findViewById(R.id.indicator)
    }

  }

  interface ShowAdapterInterface : Serializable {
    fun expandPlayback(show: Show, position: Int)
    fun play(show: Show, playback: Playback?)
    fun loadMore(showId: String?, offset: Int, limit: Int)
    fun addMoreDataListener(data: OnMoreData)
  }

  interface OnMoreData : Serializable {
    fun showMoreData(showId :String, data: LoadMoreDataResult)
  }
}
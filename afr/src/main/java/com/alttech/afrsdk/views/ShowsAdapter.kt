package com.alttech.afrsdk.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.alttech.afrsdk.R
import com.alttech.afrsdk.data.Playback
import com.alttech.afrsdk.data.PlaybackList
import com.alttech.afrsdk.data.Show
import com.alttech.afrsdk.loadUrl

/**
 * Created by bubu on 19/10/2017.
 */

class ShowsAdapter(val data: List<*>, val adapterInterface: ShowAdapterInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  val SHOW_TYPE = 1
  private val SHOW_PLAYBACKS = 2

  override fun getItemViewType(position: Int): Int {
    when (data[position]) {
      is Show -> return SHOW_TYPE
      is PlaybackList -> return SHOW_PLAYBACKS
      else -> return 0
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    when (viewType) {
      SHOW_TYPE -> return ShowHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_shows, parent, false))
      else -> return PlaybackHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_shows_playbacks, parent, false))
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ShowHolder -> {
        val show = data[position] as Show
        show.imgUrl?.let { holder.img?.loadUrl(it) }
        show.name?.let { holder.showName?.text = it }
        show.createdAt?.let { holder.lastEpisode?.text = it }
      }

      is PlaybackHolder -> {
        val playbackList = data[position] as PlaybackList
        println(">>>>>>>>"+ playbackList.itemColumn)
        holder.columnList.forEachIndexed { index, relativeLayout ->
          if (index == playbackList.itemColumn)
            relativeLayout?.visibility = View.VISIBLE
          else
            relativeLayout?.visibility = View.INVISIBLE
        }
        holder.pageCounter?.text = "Page ${playbackList.currentPage} of ${playbackList.totalPages}"

        if (playbackList.currentPage == 1) holder.previous?.visibility = View.INVISIBLE
        else holder.previous?.visibility = View.VISIBLE

        if (playbackList.currentPage == playbackList.totalPages) holder.next?.visibility = View.INVISIBLE
        else holder.next?.visibility = View.VISIBLE

      }
    }

  }

  override fun getItemCount() = data.size

  inner class ShowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var img: ImageView? = null
    var showName: TextView? = null
    var lastEpisode: TextView? = null


    init {
      img = itemView.findViewById<ImageView>(R.id.show_img)
      showName = itemView.findViewById<TextView>(R.id.show_name)
      lastEpisode = itemView.findViewById<TextView>(R.id.last_episode_date)
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

    private var recyclerView: RecyclerView? = null

    var pageCounter: TextView? = null

    var next: Button? = null
    var previous: Button? = null


    init {
      column1 = itemView.findViewById<RelativeLayout>(R.id.column_1)
      column2 = itemView.findViewById<RelativeLayout>(R.id.column_2)
      column3 = itemView.findViewById<RelativeLayout>(R.id.column_3)
      columnList = arrayListOf(column1, column2, column3)

      recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)

      pageCounter = itemView.findViewById<TextView>(R.id.page_count)
      next = itemView.findViewById<Button>(R.id.next)
      previous = itemView.findViewById<Button>(R.id.previous)

    }

  }

  interface ShowAdapterInterface {
    fun expandPlayback(show: Show, position: Int)
    fun playLastEpisode(playback: Playback)
  }
}
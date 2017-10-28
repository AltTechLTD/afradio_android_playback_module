package com.alttech.afrsdk.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
  val SHOW_PLAYBACKS = 2

  override fun getItemViewType(position: Int): Int {
    when (data[position]) {
      is Show -> return SHOW_TYPE
      is PlaybackList -> return SHOW_PLAYBACKS
      else -> return 0
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
    when (viewType) {
      SHOW_TYPE -> return ShowHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_shows, parent, false))
      SHOW_PLAYBACKS -> return PlaybackHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_shows, parent, false))
      else -> return null
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ShowHolder -> {
        val show = data[position] as Show
        show.imgUrl?.let { holder.img?.loadUrl(it) }
        show.name?.let { holder.showName?.text = "$position-$it" }
      }

      is PlaybackHolder -> {
//        val show = data[position] as Show
//        show.imgUrl?.let { holder.img?.loadUrl(it) }
//        show.name?.let { holder.showName?.text = it }
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
      lastEpisode = itemView.findViewById<TextView>(R.id.show_name)
      itemView.setOnClickListener {
        adapterInterface.expandPlayback(data[adapterPosition] as Show, adapterPosition)
      }
    }

  }

  inner class PlaybackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var img: ImageView? = null
    var showName: TextView? = null
    var lastEpisode: TextView? = null

    init {
      img = itemView.findViewById<ImageView>(R.id.show_img)
      showName = itemView.findViewById<TextView>(R.id.show_name)
      lastEpisode = itemView.findViewById<TextView>(R.id.show_name)
    }

  }

  interface ShowAdapterInterface {
    fun expandPlayback(show: Show, position: Int)
    fun playLastEpisode(playback: Playback)
  }
}
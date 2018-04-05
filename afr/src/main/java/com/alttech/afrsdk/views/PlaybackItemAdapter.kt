package com.alttech.afrsdk.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alttech.afrsdk.R
import com.alttech.afrsdk.data.Playback
import com.alttech.afrsdk.fromISO8601UTC
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by bubu on 19/10/2017.
 */

class PlaybackItemAdapter(val data: ArrayList<Playback>?, val adapterInterface: ShowsAdapter.ShowAdapterInterface?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_playback, parent, false))

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is Holder -> {
        val playback = data?.get(position)
        playback?.let {
          holder.playCount?.text = it.plays.toString() + " plays"
          val df = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
          val date = fromISO8601UTC(it.sessionDate.toString())

          val daysAgo = (System.currentTimeMillis() - date!!.time) / (24 * 60 * 60 * 1000)
          val comp = " ($daysAgo  days ago)"
          holder.title?.text = df.format(date) + comp
        }
      }
    }

  }

    override fun getItemCount() = data!!.size

  inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView? = null
    var playCount: TextView? = null


    init {
      title = itemView.findViewById(R.id.title)
      playCount = itemView.findViewById(R.id.play_count)

      itemView.setOnClickListener {
        adapterInterface?.play(data?.get(adapterPosition))
      }
    }

  }

}
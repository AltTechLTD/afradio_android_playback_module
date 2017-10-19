package com.alttech.afrsdk

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by bubu on 19/10/2017.
 */

class ShowsAdapter : RecyclerView.Adapter<ShowsAdapter.Holder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder? {
    return null
  }

  override fun onBindViewHolder(holder: Holder, position: Int) {

  }

  override fun getItemCount(): Int {
    return 0
  }

  inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

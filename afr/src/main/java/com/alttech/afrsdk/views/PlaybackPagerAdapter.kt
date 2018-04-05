package com.alttech.afrsdk.views

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.alttech.afrsdk.data.Playback

/**
 * Created by bubu on 17/03/2018.
 */

class PlaybackPagerAdapter
(fragmentManager: FragmentManager, private val list: ArrayList<Playback>, private val callback: ShowsAdapter.ShowAdapterInterface) : FragmentPagerAdapter(fragmentManager) {

  // Returns total number of pages
  override fun getCount(): Int {
    val chunk = list.size / 7.0
    return Math.ceil(chunk).toInt()
  }

  // Returns the fragment to display for that page
  override fun getItem(position: Int): Fragment? {
    val pageStart = 7 * position
    val pageEnd = pageStart + 7
    return PlaybackViewPagerListFragment.newInstance(ArrayList(list.subList(pageStart,
        if (pageEnd > list.size - 1) list.size - 1 else pageEnd)) , callback)
  }

  override fun getPageWidth(position: Int) = 0.93f

}

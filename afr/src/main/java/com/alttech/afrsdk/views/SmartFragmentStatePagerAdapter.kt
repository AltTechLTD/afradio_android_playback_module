package com.alttech.afrsdk.views

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import android.view.ViewGroup

/**
 * Created by bubu on 17/03/2018.
 */

/*
   Extension of FragmentStatePagerAdapter which intelligently caches
   all active fragments and manages the fragment lifecycles.
   Usage involves extending from SmartFragmentStatePagerAdapter as you would any other PagerAdapter.
*/
abstract class SmartFragmentStatePagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
  // Sparse array to keep track of registered fragments in memory
  private val registeredFragments = SparseArray<Fragment>()

  // Register the fragment when the item is instantiated
  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val fragment = super.instantiateItem(container, position) as Fragment
    registeredFragments.put(position, fragment)
    return fragment
  }

  // Unregister when the item is inactive
  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    registeredFragments.remove(position)
    super.destroyItem(container, position, `object`)
  }

  // Returns the fragment for the position (if instantiated)
  fun getRegisteredFragment(position: Int) = registeredFragments.get(position)
}

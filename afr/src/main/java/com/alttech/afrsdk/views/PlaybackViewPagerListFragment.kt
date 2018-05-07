package com.alttech.afrsdk.views


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alttech.afrsdk.DividerItemDecoration
import com.alttech.afrsdk.R
import com.alttech.afrsdk.data.Playback


/**
 * A simple [Fragment] subclass.
 */
class PlaybackViewPagerListFragment : Fragment() {

  val list = ArrayList<Playback>()
  var callback: ShowsAdapter.ShowAdapterInterface? = null
  var adapter: PlaybackItemAdapter? = null


  // Store instance variables based on arguments passed
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    callback = arguments?.getSerializable("callback") as ShowsAdapter.ShowAdapterInterface?
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_playback_view_pager_list, container, false)
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
    adapter = PlaybackItemAdapter(list, callback)
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter


    recyclerView.addItemDecoration(DividerItemDecoration(context!!))

    return view
  }

  fun setPlaybackData(playbackList: ArrayList<Playback>) {
    list.clear()
    list.addAll(playbackList)
    adapter?.notifyDataSetChanged()
  }

  companion object {
    fun newInstance(callback: ShowsAdapter.ShowAdapterInterface): PlaybackViewPagerListFragment {
      val fragmentFirst = PlaybackViewPagerListFragment()
      val args = Bundle()
      args.putSerializable("callback", callback)
      fragmentFirst.arguments = args
      return fragmentFirst
    }
  }

}// Required empty public constructor

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

  var list: ArrayList<Playback>? = null
  var callback: ShowsAdapter.ShowAdapterInterface? = null


  // Store instance variables based on arguments passed
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    list = arguments?.getParcelableArrayList("list")
    callback = arguments?.getSerializable("callback") as ShowsAdapter.ShowAdapterInterface?
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_playback_view_pager_list, container, false)
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
    val adapter = PlaybackItemAdapter(list, callback)
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter



    return view
  }

  companion object {
    fun newInstance(playbackList: ArrayList<Playback>, callback: ShowsAdapter.ShowAdapterInterface): PlaybackViewPagerListFragment {
      val fragmentFirst = PlaybackViewPagerListFragment()
      val args = Bundle()
      args.putParcelableArrayList("list", playbackList)
      args.putSerializable("callback", callback)
      fragmentFirst.arguments = args
      return fragmentFirst
    }
  }

}// Required empty public constructor

package com.alttech.afrsdk.views

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.alttech.afrsdk.Config
import com.alttech.afrsdk.R
import com.alttech.afrsdk.data.Playback
import com.alttech.afrsdk.data.PlaybackList
import com.alttech.afrsdk.data.Show
import com.alttech.afrsdk.data.WidgetDataResult
import com.alttech.afrsdk.toPx


class PlaybackFragment : Fragment(), View.OnClickListener, PlaybackPresenter.PlaybackView, ShowsAdapter.ShowAdapterInterface {
  override fun loadDataError() {
    retry?.visibility = View.VISIBLE
  }

  override fun getPlaybackPos() = playbackPosition

  override fun progressView(show: Boolean) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun showErrorText(txt: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getColumnSize() = columnSizeX

  var columnSizeX = 2

  var config: Config? = null

  var bottomSheetBehaviour: BottomSheetBehavior<View>? = null

  var presenter: PlaybackPresenter? = null

  var recyclerView: RecyclerView? = null

  var retry: Button? = null

  val list: ArrayList<Any?> = ArrayList()

  val adapter = ShowsAdapter(list, this)

  var playbackPosition = -1

  override fun onClick(v: View?) {
    when (v?.id) {
//      R.id.button -> {
//        bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_EXPANDED
//      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    this.config = arguments?.getSerializable("config") as Config
    presenter = PlaybackPresenter(config!!)
  }

  override fun onStart() {
    super.onStart()
    presenter?.subscribe(this)
  }

  override fun onStop() {
    presenter?.unSubscribe()
    super.onStop()
  }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.fragment_playback, container, false)

    val bottomSheet = view?.findViewById<View>(R.id.bottom_sheet);

    recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view)

    retry = view?.findViewById<Button>(R.id.retry)

    retry?.setOnClickListener({ v ->
      list.clear()
      presenter?.fetchWidgetData()
    })

    columnSizeX = 3

    val layoutManager = GridLayoutManager(context, getColumnSize())

    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        when (list[position]) {
          is Show -> return 1
          else -> return getColumnSize()
        }
      }
    }

    recyclerView?.layoutManager = layoutManager

    recyclerView?.adapter = adapter

    bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)

    bottomSheetBehaviour?.peekHeight = 80.toPx

    bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED

    presenter?.fetchWidgetData()

    return view

  }

  override fun showWidgetData(data: WidgetDataResult) {
    retry?.visibility = View.GONE

    list.clear()
    data.shows?.let { list.addAll(it) }
    adapter.notifyDataSetChanged()
  }


  override fun expandPlayback(show: Show, position: Int) {
    presenter?.getPlaybackList(show, position)
  }

  override fun playLastEpisode(playback: Playback) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removeItem(pos: Int) {
    list.removeAt(pos)
    adapter.notifyItemRemoved(pos)
  }

  override fun addPlaybackList(pos: Int, playbackList: PlaybackList) {
    if (pos > list.size) {
      list.add(playbackList)
      playbackPosition = list.size - 1
    } else {
      list.add(pos, playbackList)
      adapter.notifyItemInserted(pos)
      recyclerView?.smoothScrollToPosition(pos)
      playbackPosition = pos
    }
  }

  fun getWidth() {
    val displayMetrics = DisplayMetrics();
    activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val width = displayMetrics.widthPixels
  }

  companion object {
    fun newInstance(config: Config): Fragment {
      val args = Bundle()
      args.putSerializable("config", config)
      val fragment = PlaybackFragment()
      fragment.arguments = args
      return fragment
    }
  }
}


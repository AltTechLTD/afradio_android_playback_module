package com.alttech.afrsdk

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alttech.afrsdk.data.WidgetDataResult
import com.alttech.afrsdk.network.ApiCalls


class PlaybackFragment : Fragment(), View.OnClickListener {

  var config: Config? = null

  var bottomSheetBehaviour: BottomSheetBehavior<View>? = null


  var recyclerView: RecyclerView? = null

  override fun onClick(v: View?) {
    when (v?.id) {
//      R.id.button -> {
//        bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_EXPANDED
//      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    this.config = arguments.getSerializable("config") as Config
  }


  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater?.inflate(R.layout.fragment_playback, container, false)
    val bottomSheet = view?.findViewById<View>(R.id.bottom_sheet);
    val recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view);


    bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)
    bottomSheetBehaviour?.peekHeight = 80.toPx

    bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED

    fetchWidgetData()

    return view
  }

  private fun fetchWidgetData() {
    ApiCalls.getShows(config!!.appId!!, config!!.resId!!)
        .subscribe({ t: WidgetDataResult ->
          println(">>>>>>>>>>>>>>>>>>>> this is the name of the station ${t.name}")
        }, {
          it.printStackTrace()
        })
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


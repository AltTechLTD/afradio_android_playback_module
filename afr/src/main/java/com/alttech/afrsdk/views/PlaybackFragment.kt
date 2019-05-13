package com.alttech.afrsdk.views

import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import com.alttech.afrsdk.*
import com.alttech.afrsdk.data.*
import com.alttech.afrsdk.player.RadioListener
import com.alttech.afrsdk.player.StreamManagerImpl
import com.squareup.picasso.Picasso


class PlaybackFragment : Fragment(), View.OnClickListener, PlaybackPresenter.PlaybackView,
    ShowsAdapter.ShowAdapterInterface, RadioListener.PlayUpdaterListener, RadioListener {


  //player views
  var showName: TextView? = null
  var showNameSmall: TextView? = null
  var progressTracker: TextView? = null
  var sessionDate: RelativeTimeTextView? = null
  var sessionDate2: RelativeTimeTextView? = null
  var expDate: TextView? = null
  var coverBig: ImageView? = null
  var coverSmall: ImageView? = null
  var seekBar: SeekBar? = null
  var timer: TextView? = null
  var skipBack: ImageView? = null
  var skipForward: ImageView? = null
  var playpauseBig: ImageView? = null
  var playpauseSmall: ImageView? = null

  var loading: ProgressBar? = null
  var layout: RelativeLayout? = null

  var loadSmall: ProgressBar? = null
  var loadBig: ProgressBar? = null

  var filterEditText: EditText? = null


  var columnSizeX = 2

  var config: Config? = null

  var bottomSheetBehaviour: BottomSheetBehavior<View>? = null

  var presenter: PlaybackPresenter? = null

  var recyclerView: RecyclerView? = null

  var retry: Button? = null

  val originalList: ArrayList<Any?> = ArrayList()
  val list: ArrayList<Any?> = ArrayList()

  lateinit var adapter:ShowsAdapter
  val listeners = ArrayList<ShowsAdapter.OnMoreData>()


  var playbackPosition = -1

  override fun onClick(v: View?) {
    when (v?.id) {
//      R.id.button -> {
//        bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_EXPANDED
//      }
    }
  }

  override fun addMoreDataListener(data: ShowsAdapter.OnMoreData) {
    listeners.add(data)
  }

  override fun showMoreData(showId: String, data: LoadMoreDataResult) {
    listeners.forEach { it.showMoreData(showId, data) }
  }


  override fun play(show: Show, playback: Playback?) {
    playback?.let {
      StreamManagerImpl.playRequest(show, playback)
      setViews(show, playback)
    }
  }


  fun setViews(show: Show, playback: Playback) {

    showName?.text = show.name
    showNameSmall?.text = show.name
    sessionDate?.setReferenceTime(fromISO8601UTC(playback.sessionDate!!)!!.time)
    sessionDate2?.setReferenceTime(fromISO8601UTC(playback.sessionDate!!)!!.time)
    coverBig?.loadUrl(show.imgUrl!!)
    coverSmall?.loadUrl(show.imgUrl!!)
    expDate?.text = fromISO8601UTC(playback.sessionDate!!)?.toHumanReadable()

//    timer = bottomSheet?.findViewById(R.id.progress_text)
//    skipBack = bottomSheet?.findViewById(R.id.rewind_button)
//    skipForward = bottomSheet?.findViewById(R.id.forward_action)
//    playpauseBig = bottomSheet?.findViewById(R.id.play_action)
//    playpauseSmall = bottomSheet?.findViewById(R.id.small_play)

  }


  override fun loadDataError() {
    retry?.visibility = View.VISIBLE
    loading?.visibility = View.GONE
    layout?.visibility = View.VISIBLE
  }

  override fun getPlaybackPos() = playbackPosition

  override fun progressView(show: Boolean) {
  }

  override fun showErrorText(txt: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getColumnSize() = columnSizeX

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Picasso.get().setLoggingEnabled(true)

    this.config = arguments?.getSerializable("config") as Config
    presenter = PlaybackPresenter(config!!)
    adapter = ShowsAdapter(list, this)
    StreamManagerImpl.setup(context!!)
  }

  override fun onStart() {
    super.onStart()
    presenter?.subscribe(this)
    StreamManagerImpl.addOnPlayUpdateListener(this)
    StreamManagerImpl.setRadiolistener(this)
  }

  override fun onStop() {
    StreamManagerImpl.removeOnPlayUpdateListener(this)
    presenter?.unSubscribe()
    StreamManagerImpl.removelistener(this)
    super.onStop()
  }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.fragment_playback, container, false)

    val bottomSheet = view?.findViewById<View>(R.id.bottom_sheet);

    val peekView = bottomSheet?.findViewById<RelativeLayout>(R.id.peek_view)

    showName = bottomSheet?.findViewById(R.id.program_name_text_view)
    showNameSmall = bottomSheet?.findViewById(R.id.small_title)
    progressTracker = bottomSheet?.findViewById(R.id.progress_tracker)
    sessionDate = bottomSheet?.findViewById(R.id.date)
    sessionDate2 = bottomSheet?.findViewById(R.id.date2)
    expDate = bottomSheet?.findViewById(R.id.expdate)
    coverBig = bottomSheet?.findViewById(R.id.cover)
    coverSmall = bottomSheet?.findViewById(R.id.small_image)
    seekBar = bottomSheet?.findViewById(R.id.seek_bar)
    timer = bottomSheet?.findViewById(R.id.progress_text)
    skipBack = bottomSheet?.findViewById(R.id.rewind_button)
    skipForward = bottomSheet?.findViewById(R.id.forward_action)
    playpauseBig = bottomSheet?.findViewById(R.id.play_action)
    playpauseSmall = bottomSheet?.findViewById(R.id.small_play)

    loadBig = bottomSheet?.findViewById(R.id.loading_big)
    loadSmall = bottomSheet?.findViewById(R.id.loading_small)


    recyclerView = view?.findViewById(R.id.recycler_view)
    filterEditText = view?.findViewById(R.id.search)

    layout = view?.findViewById(R.id.layout)
    loading = view?.findViewById(R.id.loading)


    filterEditText?.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(p0: Editable?) {
      }

      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      }

      override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        list.clear()
        originalList.forEach {
          when (it) {is Show -> if (it.name?.contains(p0!!)!!) list.add(it)
          }
        }
        adapter.notifyDataSetChanged()
      }

    })


    retry = view?.findViewById(R.id.retry)

    retry?.setOnClickListener { v ->
      list.clear()
      presenter?.fetchWidgetData()
    }

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

//    bottomSheetBehaviour?.peekHeight = 80.toPx

    bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED

    bottomSheetBehaviour?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
      override fun onSlide(bottomSheet: View, slideOffset: Float) {
      }

      override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_EXPANDED) peekView?.visibility = View.GONE
        else peekView?.visibility = View.VISIBLE
      }

    })

    presenter?.fetchWidgetData()


    seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, progerss: Int, fromUser: Boolean) {
        progressTracker?.text = "${DateUtils.formatElapsedTime(progerss.toLong())}  /  ${DateUtils.formatElapsedTime(seekBar.max.toLong())}"
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {
        progressTracker?.animate()?.cancel()
        progressTracker?.alpha = 1f
        progressTracker?.setVisibility(true)
      }

      override fun onStopTrackingTouch(seekBar: SeekBar) {

        StreamManagerImpl.seek(seekBar.progress.toLong())

        progressTracker!!.animate()
            .alpha(0f)
            .setDuration(500)
            .setStartDelay(500)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
              progressTracker?.setVisibility(false)
              progressTracker?.alpha = 1f
            }.start()
      }
    })
    view.viewTreeObserver.addOnGlobalLayoutListener {

      val r = Rect()
      view.getWindowVisibleDisplayFrame(r);
      val screenHeight = view.getRootView().getHeight();

      val keypadHeight = screenHeight - r.bottom;


//      if (keypadHeight > screenHeight * 0.15 && bottomSheetBehaviour?.state == BottomSheetBehavior.STATE_COLLAPSED) {
//        bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_HIDDEN
//      } else {
//        bottomSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
//      }

    }

    return view

  }


  override fun showWidgetData(data: WidgetDataResult) {
    retry?.visibility = View.GONE
    loading?.visibility = View.GONE
    layout?.visibility = View.VISIBLE


    list.clear()
    data.shows?.let {
      list.addAll(it)
      originalList.addAll(it)
    }
    adapter.notifyDataSetChanged()
  }


  override fun loadMore(showId: String?, offset: Int, limit: Int) {
    presenter?.loadMore(showId, offset, limit)
  }


  override fun expandPlayback(show: Show, position: Int) {
    presenter?.getPlaybackList(show, position)
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


  override fun update(progress: Float, current: Long, total: Long) {
    seekBar?.post {
      seekBar?.max = total.toInt()
      seekBar?.progress = current.toInt()
    }

    timer?.text = "${DateUtils.formatElapsedTime(current / 1000)}  /  ${DateUtils.formatElapsedTime(total / 1000)}"
  }

  override fun onRadioLoading() {
    playpauseSmall?.setVisibility(false)
    playpauseBig?.setVisibility(false)

    loadSmall?.setVisibility(true)
    loadBig?.setVisibility(true)
  }

  override fun onRadioSwitching() {
  }

  override fun onRadioConnected() {
  }

  override fun onRadioStarted(mime: String, sampleRate: Int, channels: Int, duration: Long) {
  }

  override fun onRadioPlay() {
    playpauseSmall?.setVisibility(true)
    playpauseBig?.setVisibility(true)

    loadSmall?.setVisibility(false)
    loadBig?.setVisibility(false)

    playpauseBig?.background = ContextCompat.getDrawable(context!!, R.drawable.ic_pause_circle_filled_black_24dp);
    playpauseSmall?.background = ContextCompat.getDrawable(context!!, R.drawable.ic_pause_circle_filled_black_24dp);

  }

  override fun onRadioPaused() {
    playpauseSmall?.setVisibility(true)
    playpauseBig?.setVisibility(true)

    playpauseBig?.background = ContextCompat.getDrawable(context!!, R.drawable.ic_play_arrow_black_24dp);
    playpauseSmall?.background = ContextCompat.getDrawable(context!!, R.drawable.ic_play_arrow_black_24dp);
  }

  override fun onRadioStopped() {
  }

  override fun playingAd() {
  }

  override fun onError() {
  }

  override fun metaData(meta: Parcelable) {
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


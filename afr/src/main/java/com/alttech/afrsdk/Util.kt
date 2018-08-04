package com.alttech.afrsdk

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by bubu on 19/10/2017.
 */

//extension function
val Int.toPx: Int
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun ImageView.loadUrl(url: String) {
  println("img url .>>>>>>>>>>>>>>"+ url)
  Picasso.with(context).load(url).into(this)
}

fun fromISO8601UTC(dateStr: String): Date? {
  val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
  try {
    return df.parse(dateStr)
  } catch (e: ParseException) {
    e.printStackTrace()
  }
  return null
}

fun Date.toHumanReadable(): String {
  val dateFormat = SimpleDateFormat("EEE, dd MMM, yyyy, H:mm a", Locale.getDefault());
  return dateFormat.format(this.getTime())
}


fun View.setVisibility(visible: Boolean) {
  this.visibility = if (visible) View.VISIBLE else View.GONE
}


class DividerItemDecoration : RecyclerView.ItemDecoration {

  private var mDivider: Drawable? = null

  /**
   * Default divider will be used
   */
  constructor(context: Context) {
    val styledAttributes = context.obtainStyledAttributes(ATTRS)
    mDivider = styledAttributes.getDrawable(0)
    styledAttributes.recycle()
  }

  /**
   * Custom divider will be used
   */
  constructor(context: Context, resId: Int) {
    mDivider = ContextCompat.getDrawable(context, resId)
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
    val left = parent.paddingLeft
    val right = parent.width - parent.paddingRight

    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)

      val params = child.layoutParams as RecyclerView.LayoutParams

      val top = child.bottom + params.bottomMargin
      val bottom = top + mDivider!!.intrinsicHeight

      mDivider?.setBounds(left, top, right, bottom)
      mDivider?.draw(c)
    }
  }

  companion object {

    private val ATTRS = intArrayOf(android.R.attr.listDivider)
  }
}

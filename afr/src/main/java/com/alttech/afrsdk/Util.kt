package com.alttech.afrsdk

import android.content.res.Resources
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
  Picasso.with(context).load(url).into(this)
}

fun fromISO8601UTC(dateStr: String): Date? {
  val tz = TimeZone.getTimeZone("UTC")
  val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault())
  df.timeZone = tz
  try {
    return df.parse(dateStr)
  } catch (e: ParseException) {
    e.printStackTrace()
  }
  return null
}


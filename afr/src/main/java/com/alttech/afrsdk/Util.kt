package com.alttech.afrsdk

import android.content.res.Resources
import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Created by bubu on 19/10/2017.
 */

//extension function
val Int.toPx: Int
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun ImageView.loadUrl(url: String) {
  Picasso.with(context).load(url).into(this)
}


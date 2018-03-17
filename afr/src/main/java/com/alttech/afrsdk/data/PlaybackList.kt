package com.alttech.afrsdk.data

class PlaybackList(var itemColumn: Int) {
  var playbacks: List<Playback>? = null
  var currentPage: Int = 1
  var totalPages: Int = 0
}
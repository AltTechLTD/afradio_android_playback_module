package com.alttech.afrsdk.data

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by bubu on 20/10/2017.
 */

class Playback(
    @Json(name = "_id")
    var id: String?,
    @Json(name = "length")
    var length: Int?,
    @Json(name = "monetize")
    var monetize: Boolean?,
    @Json(name = "plays")
    var plays: Int?,
    @Json(name = "sessionDate")
    var sessionDate: String?,
    @Json(name = "streamUrl")
    var streamUrl: String?,
    var playing: Boolean = false
) :Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeValue(length)
        parcel.writeValue(monetize)
        parcel.writeValue(plays)
        parcel.writeString(sessionDate)
        parcel.writeString(streamUrl)
        parcel.writeByte(if (playing) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Playback> {
        override fun createFromParcel(parcel: Parcel): Playback = Playback(parcel)
        override fun newArray(size: Int): Array<Playback?> = arrayOfNulls(size)
    }

}

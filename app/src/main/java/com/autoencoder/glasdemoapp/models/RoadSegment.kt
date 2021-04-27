package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoadSegment(
    @SerializedName("start") val start: Int,
    @SerializedName("end") val end: Int,
    @SerializedName("lane-count") val laneCount: Int
): Parcelable
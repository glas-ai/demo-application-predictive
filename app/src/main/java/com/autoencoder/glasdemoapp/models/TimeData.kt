package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeData(
    @SerializedName(
        "leave-home-time"
    ) val timeData: List<TimeDataEntry>?
) : Parcelable

@Parcelize
data class TimeDataEntry(
    @SerializedName("day") val day: String,
    @SerializedName("probability") val probability: Float,
    @SerializedName("time") val time: Long
) : Parcelable

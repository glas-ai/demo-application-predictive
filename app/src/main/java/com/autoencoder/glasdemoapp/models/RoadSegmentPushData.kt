package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoadSegmentPushData(
    @SerializedName("query_id") val queryId: String,
    @SerializedName("data") val data: Map<String, List<RoadSegment>>
): Parcelable

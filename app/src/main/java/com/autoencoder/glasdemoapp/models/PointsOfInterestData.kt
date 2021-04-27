package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PointsOfInterestData(
    @SerializedName("favourite-P.O.I.s") val pointsOfInterests: List<PointOfInterest>
) : Parcelable

@Parcelize
data class PointOfInterest(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("latitude") val latitude: Float,
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("visit") val visitCount: Int
) : Parcelable

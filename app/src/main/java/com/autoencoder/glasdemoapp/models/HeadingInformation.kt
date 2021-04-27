package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeadingInformation(
    @SerializedName("tag") val tag: String,
    @SerializedName("probability") val probability: Float,
    @SerializedName("route") val route: Route
) : Parcelable

@Parcelize
data class Route(
    @SerializedName("legs") val legs: List<Leg>
): Parcelable

@Parcelize
data class Leg(
    @SerializedName("type") val type: String,
    @SerializedName("waypoints") val waypoints: List<Waypoint>
): Parcelable

@Parcelize
data class Waypoint(
    @SerializedName("location") val location: Location
): Parcelable

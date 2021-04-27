package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GasStationsData(
    @SerializedName("favourite-gasstation") val gasStations: List<GasStation>
) : Parcelable

@Parcelize
data class GasStation(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("latitude") val latitude: Float,
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("visit") val visitCount: Int
) : Parcelable

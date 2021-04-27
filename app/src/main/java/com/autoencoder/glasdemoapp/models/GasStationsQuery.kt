package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GasStationsQuery (
    @SerializedName("query") val query: GasStationsQueryData,
    @SerializedName("message_id") val messageId: String = "{1e4b6307-2e29-4d1a-9add-a0eac90c75fe}",
    @SerializedName("api_info") val apiInfo: ApiInfo = ApiInfo()
) : Parcelable

@Parcelize
data class GasStationsQueryData(
    @SerializedName("data") val data: GasStations
) : Parcelable

@Parcelize
data class GasStations(
    @SerializedName("favourite-gasstation") val favouriteGasStations: Empty = Empty()
) : Parcelable

package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PointsOfInterestQuery(
    @SerializedName("query") val query: PointsOfInterestQueryData,
    @SerializedName("message_id") val messageId: String = "{90221a60-1a1a-434a-9e1b-db0c77374324}",
    @SerializedName("api_info") val apiInfo: ApiInfo = ApiInfo()
) : Parcelable

@Parcelize
data class PointsOfInterestQueryData(
    @SerializedName("data") val data: PointsOfInterest
) : Parcelable

@Parcelize
data class PointsOfInterest(
    @SerializedName("favourite-P.O.I.s") val favPOIs: Empty = Empty()
) : Parcelable

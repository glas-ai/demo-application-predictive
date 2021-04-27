package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SupermarketsQuery (
    @SerializedName("query") val query: SupermarketsQueryData,
    @SerializedName("message_id") val messageId: String = "{ea732c83-92f6-41c4-a09d-8b412071c6c3}",
    @SerializedName("api_info") val apiInfo: ApiInfo = ApiInfo()
) : Parcelable

@Parcelize
data class SupermarketsQueryData(
    @SerializedName("data") val data: Supermarkets
) : Parcelable

@Parcelize
data class Supermarkets(
    @SerializedName("favourite-supermarket") val favouriteSupermarkets: Empty = Empty()
) : Parcelable

package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubscribeQuery(
    @SerializedName("api_info") val apiInfo: ApiInfo = ApiInfo(),
    @SerializedName("message_id") val messageId: String = "SUBSCRIBE_ID",
    @SerializedName("query") val queryType: QueryType
) : Parcelable
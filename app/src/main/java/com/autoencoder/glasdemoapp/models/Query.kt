package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Query(
    @SerializedName("message_id")val messageId: String,
    @SerializedName("query") val query: QueryData,
    @SerializedName("api_info")val apiInfo: ApiInfo = ApiInfo()
): Parcelable

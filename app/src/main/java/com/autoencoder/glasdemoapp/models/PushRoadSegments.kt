package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PushRoadSegments(
    @SerializedName("api_info")val apiInfo: ApiInfo = ApiInfo(),
    @SerializedName("message_id")val messageId: String = "PUSH-ROAD-SEGMENTS-ID",
    @SerializedName("query") val query: QueryData
): Parcelable
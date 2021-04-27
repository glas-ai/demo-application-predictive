package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QueryData(
    @SerializedName("data") val data : Map<String, EmptyClass>
): Parcelable
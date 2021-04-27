package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceAvailability(
    @SerializedName("state") val state: String? = null,
    @SerializedName("type") val type: String,
    @SerializedName("availability") val availability: String? = null
): Parcelable
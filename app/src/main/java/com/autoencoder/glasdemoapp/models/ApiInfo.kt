package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApiInfo(
    @SerializedName("type") val type: String = "lite_machine_readable",
    @SerializedName("version") val version: String = "1.2.0"
): Parcelable

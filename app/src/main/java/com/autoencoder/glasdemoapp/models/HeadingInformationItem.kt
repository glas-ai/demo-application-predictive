package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeadingInformationItem(
    val headingNo: Int,
    val headingName: String,
    val lat: Float,
    val lng: Float,
    val tag: String,
    val probability: Float
): Parcelable

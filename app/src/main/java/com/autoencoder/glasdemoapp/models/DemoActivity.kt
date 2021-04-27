package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DemoActivityItem(
    val service: Service,
    val percentage: Int
): Parcelable
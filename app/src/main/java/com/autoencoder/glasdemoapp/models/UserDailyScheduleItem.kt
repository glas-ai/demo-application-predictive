package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.autoencoder.glasdemoapp.R
import kotlinx.android.parcel.Parcelize

enum class Day(
    @DrawableRes val drawable: Int,
    @StringRes val displayName: Int,
    val dayName: String
) {
    MONDAY(R.drawable.mon, R.string.monday, "monday"),
    TUESDAY(R.drawable.tue, R.string.tuesday, "tuesday"),
    WEDNESDAY(R.drawable.wed, R.string.wednesday, "wednesday"),
    THURSDAY(R.drawable.thu, R.string.thursday, "thursday"),
    FRIDAY(R.drawable.friday, R.string.friday, "friday"),
    SATURDAY(R.drawable.sat, R.string.saturday, "saturday"),
    SUNDAY(R.drawable.sun, R.string.sunday, "sunday");

    companion object {
        fun getDayFromString(string: String) = values().find { it.dayName == string } ?: MONDAY
    }
}

@Parcelize
data class UserDailyScheduleItem(
    val day: Day,
    val predictionAccuracy: Float,
    val time: String,
    val isArrival: Boolean = false
) : Parcelable
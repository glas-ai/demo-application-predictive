package com.autoencoder.glasdemoapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.autoencoder.glasdemoapp.R

enum class Service(
    @StringRes val title: Int,
    @DrawableRes val drawable: Int,
    val type: String,
    @StringRes val criteria: Int
) {
    HOME_LOCATION(
        R.string.home_location,
        R.drawable.home,
        "home-address",
        R.string.home_work_location_criteria
    ),
    WORK_LOCATION(
        R.string.work_location,
        R.drawable.work,
        "work-address",
        R.string.home_work_location_criteria
    ),
    POINTS_OF_INTEREST(
        R.string.points_of_interest,
        R.drawable.points_of_interest,
        "favourite-P.O.I.",
        R.string.poi_criteria
    ),
    HEADING_INFORMATION(
        R.string.heading_information,
        R.drawable.heading_information,
        "heading",
        R.string.heading_criteria
    ),
    GAS_STATIONS(
        R.string.gas_station_brands,
        R.drawable.gas_station,
        "favourite-gasstation",
        R.string.gas_station_criteria
    ),
    SUPERMARKETS(
        R.string.supermarket_brands,
        R.drawable.supermarket,
        "favourite-supermarket",
        R.string.supermarket_criteria
    ),
    USER_DAILY_SCHEDULE(
        R.string.user_daily_schedule,
        R.drawable.user_daily_schedule,
        "leave-home-time",
        R.string.user_daily_criteria
    );

    companion object {
        fun getServiceFromType(type: String) = values().find { it.type == type }
    }
}
package com.autoencoder.glasdemoapp.models

import androidx.annotation.StringRes
import com.autoencoder.glasdemoapp.R

enum class FeatureRequirement(@StringRes val typeName: Int, @StringRes val description: Int) {
    INTERNET(R.string.internet, R.string.internet_explication),
    GPS(R.string.gps, R.string.gps_explication)
}
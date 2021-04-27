package com.autoencoder.glasdemoapp.main.about

import androidx.annotation.StringRes
import com.autoencoder.glasdemoapp.R

enum class AboutTabs(@StringRes val title: Int) {
    ABOUT_GLAS(R.string.about_glas_ai),
    QT_LICENSE(R.string.qt_license)
}
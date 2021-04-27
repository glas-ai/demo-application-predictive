package com.autoencoder.glasdemoapp.shared.utils.bindingAdapters

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visible")
fun View.isVisible(isVisible: Boolean?) {
    isVisible?.let {
        visibility = if (it) View.VISIBLE else View.GONE
    }
}
package com.autoencoder.glasdemoapp.shared.utils.bindingAdapters

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.autoencoder.glasdemoapp.shared.utils.getProgressColor

@BindingAdapter("progress_color")
fun CircularProgressView.setProgressTintColor(percentage: Int?) {
    percentage?.let { progress ->
        color = ContextCompat.getColor(context, progress.getProgressColor())
        this.progress = progress.toFloat()
    }
}
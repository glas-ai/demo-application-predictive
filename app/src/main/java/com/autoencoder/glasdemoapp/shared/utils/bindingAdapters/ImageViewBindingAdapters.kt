package com.autoencoder.glasdemoapp.shared.utils.bindingAdapters

import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.models.Service
import com.autoencoder.glasdemoapp.shared.utils.getProgressColor
import org.jetbrains.anko.padding

private const val EXTRA_PADDING = 18
private const val PADDING = 16
private const val SMALLER_PADDING = 13

@BindingAdapter("imageResId")
fun ImageView.setImageResourceId(@DrawableRes id: Int?) {
    id?.let {
        setImageResource(it)
    }
}

@BindingAdapter("progress_color")
fun ImageView.setProgressColorTint(percentage: Int?) {
    percentage?.let { progress ->
        setColorFilter(
            ContextCompat.getColor(context, progress.getProgressColor()),
            PorterDuff.Mode.SRC_IN
        )
    }
}

@BindingAdapter("enabled_percentage")
fun ImageView.setEnabledByPercentage(percentage: Int?) {
    percentage?.let { progress ->
        if (progress == 100) {
            setColorFilter(
                ContextCompat.getColor(context, R.color.green_data),
                PorterDuff.Mode.SRC_IN
            )
        }
    }
}

@BindingAdapter("adjust_padding")
fun ImageView.adjustPadding(service: Service?) {
    service?.let { serviceType ->
        val paddingToDp = context.resources.displayMetrics.density
        padding = when (serviceType) {
            Service.HOME_LOCATION, Service.WORK_LOCATION -> (paddingToDp * EXTRA_PADDING).toInt()
            Service.SUPERMARKETS -> (paddingToDp * SMALLER_PADDING).toInt()
            else -> (paddingToDp * PADDING).toInt()
        }
    }
}
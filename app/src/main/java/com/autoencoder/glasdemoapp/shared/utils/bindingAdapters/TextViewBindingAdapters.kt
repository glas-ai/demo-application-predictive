package com.autoencoder.glasdemoapp.shared.utils.bindingAdapters

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.shared.utils.getProgressColor
import kotlin.math.roundToInt

const val HEADING_ONE = 0
const val HEADING_TWO = 1
const val HEADING_THREE = 2

@BindingAdapter("progress_color")
fun TextView.setColorByPercentage(percentage: Int?) {
    percentage?.let { progress ->
        setTextColor(ContextCompat.getColor(context, progress.getProgressColor()))
    }
}

@BindingAdapter("heading_color")
fun TextView.setHeadingColor(headingNo: Int?) {
    headingNo?.let {
        setTextColor(
            ContextCompat.getColor(
                context, when (it) {
                    HEADING_ONE -> R.color.green_data
                    HEADING_TWO -> R.color.blue_data
                    HEADING_THREE -> R.color.orange_data
                    else -> R.color.green_data
                }
            )
        )
    }
}

@BindingAdapter("percentage")
fun TextView.setProbability(probability: Float?) {
    probability?.let {
        text = context.getString(R.string.percentage, (it * 100).roundToInt())
    }
}

@BindingAdapter("day_text", "is_arrival", requireAll = true)
fun TextView.setDayText(@StringRes dayTextId: Int?, isArrival: Boolean = false) {
    dayTextId?.let {
        text = context.getString(
            if (isArrival) R.string.arrival_time else R.string.departure_time,
            context.getString(it)
        )
    }
}
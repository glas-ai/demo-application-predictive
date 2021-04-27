package com.autoencoder.glasdemoapp.shared.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.annotation.ColorRes
import com.autoencoder.glasdemoapp.R

@ColorRes
fun Int.getProgressColor() =
    when {
        this < 45 -> {
            R.color.orange_data
        }
        this < 100 -> {
            R.color.blue_data
        }
        else -> {
            R.color.green_data
        }
    }

fun View.generateBitmap(): Bitmap {
    val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(measureSpec, measureSpec)
    layout(0, 0, measuredWidth, measuredHeight)
    val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(Color.TRANSPARENT)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}
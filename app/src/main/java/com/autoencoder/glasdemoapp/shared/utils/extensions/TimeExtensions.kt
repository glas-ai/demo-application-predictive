package com.autoencoder.glasdemoapp.shared.utils.extensions

fun Long.convertToHour(): String {
    val minutes = this / 60000 % 60
    val hours = this / (60000 * 60) % 24
    return String.format("%d:%02d", hours, minutes)
}
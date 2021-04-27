package com.autoencoder.glasdemoapp.models

import com.google.gson.annotations.SerializedName

data class NotificationResponse<T>(
    @SerializedName("api_info") val apiInfo: ApiInfo,
    @SerializedName("message_id") val messageId: String,
    @SerializedName("notification") val notification: Notification<T>
)

data class Notification<T>(
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: List<T>
)

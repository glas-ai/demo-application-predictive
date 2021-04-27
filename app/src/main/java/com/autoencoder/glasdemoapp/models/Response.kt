package com.autoencoder.glasdemoapp.models

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("api_info") val apiInfo: ApiInfo,
    @SerializedName("message_id") val messageId: String,
    @SerializedName("reply", alternate = ["push"]) val reply: ResponseData<T>,
)

data class ResponseData<T>(
    @SerializedName("query_id") val queryId: String? = null,
    @SerializedName("data") val data: T
)
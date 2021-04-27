package com.autoencoder.glasdemoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AboutInfo(
    @SerializedName("author") val author: Author,
    @SerializedName("bots") val botInfo: BotInfo,
    @SerializedName("company") val company: String,
    @SerializedName("name") val name: String,
    @SerializedName("revision") val revision: String,
    @SerializedName("version") val version: String
): Parcelable

@Parcelize
data class BotInfo(
    @SerializedName("glas") val glasInfo: GlasInfo
): Parcelable

@Parcelize
data class GlasInfo(
    @SerializedName("ais") val aisInfo: List<ComponentInfo>,
    @SerializedName("capabilities") val capabilities: List<ComponentInfo>,
    @SerializedName("enablers") val enablers: List<ComponentInfo>
): Parcelable

@Parcelize
data class ComponentInfo(
    @SerializedName("author") val author: Author,
    @SerializedName("company") val company: String,
    @SerializedName("name") val name: String,
    @SerializedName("revision") val revision: String,
    @SerializedName("version") val version: String
): Parcelable

@Parcelize
data class Author(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String
): Parcelable
package com.autoencoder.glasdemoapp.main.about.glas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.autoencoder.glasdemoapp.BuildConfig
import com.autoencoder.glasdemoapp.models.AboutInfo
import com.autoencoder.glasdemoapp.models.ComponentInfo
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import com.google.gson.reflect.TypeToken
import glas.ai.sdk.GlasAI

private const val NONE = "none"
private const val MODULE = "Module: "
private const val REVISION = "Revision: "
private const val VERSION = "Version: "
private const val REVISION_CHARACTER_COUNT = 15
private const val NL = "\n"

class AboutGlasViewModel : BaseViewModel() {

    private val _applicationVersion = MutableLiveData<String>()
    val applicationVersion: LiveData<String> = _applicationVersion

    private val _buildComponents = MutableLiveData<String>()
    val buildComponents: LiveData<String> = _buildComponents

    private val _capabilities = MutableLiveData<String>()
    val capabilities: LiveData<String> = _capabilities

    private val _enablers = MutableLiveData(NONE)
    val enablers: LiveData<String> = _enablers

    init {
        loadInfo()
    }

    private fun List<ComponentInfo>.toModuleString() =
        StringBuilder().apply {
            this@toModuleString.forEach { componentInfo ->
                append(MODULE).append(componentInfo.name).append(NL)
                append(REVISION).append(componentInfo.revision.take(REVISION_CHARACTER_COUNT)).append(NL)
                append(VERSION).append(componentInfo.version).append(NL).append(NL)
            }
        }.toString()

    private fun loadInfo() {
        val aboutInfo =
            gson.fromJson<AboutInfo>(GlasAI.info(), object : TypeToken<AboutInfo>() {}.type)
        _applicationVersion.value = BuildConfig.VERSION_NAME
        val buildInfoString = aboutInfo.botInfo.glasInfo.aisInfo.toModuleString()
        aboutInfo.botInfo.glasInfo.apply {
            _capabilities.value = capabilities.toModuleString()
            _enablers.value = enablers.toModuleString()
        }
        _buildComponents.value = buildInfoString +
                MODULE + aboutInfo.name + NL +
                REVISION + aboutInfo.revision.take(REVISION_CHARACTER_COUNT) + NL +
                VERSION + aboutInfo.version

    }
}

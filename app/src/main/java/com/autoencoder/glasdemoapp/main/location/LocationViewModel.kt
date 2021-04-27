package com.autoencoder.glasdemoapp.main.location

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import glas.ai.sdk.NotificationsEngine

enum class LocationScreen(@DrawableRes val icon: Int, @StringRes val title: Int) {
    HOME(R.drawable.home, R.string.home_location),
    WORK(R.drawable.work, R.string.work_location)
}

abstract class LocationViewModel(locationScreen: LocationScreen) : BaseViewModel() {

    val icon = MutableLiveData(locationScreen.icon)
    val title = MutableLiveData(locationScreen.title)
    val lat = MutableLiveData(0f)
    val lng = MutableLiveData(0f)

    private val _markerLocation = MutableLiveData<Location>()
    val markerLocation: LiveData<Location> = _markerLocation

    private val responseType = object : TypeToken<Response<Map<String, Location>>>() {}.type

    abstract fun requestMarkerLocation()

    protected val onNotificationsErrorListener = object : NotificationsEngine.OnErrorListener {

        override fun onError(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }
    }

    protected val onDataAvailableListener = object : DataIOEngine.OnDataAvailableListener {

        override fun onDataAvailable(dataJson: String) {
            gson.fromJson<Response<Map<String, Location>>>(dataJson, responseType)?.also {
                it.reply.data.values.firstOrNull()?.let { location ->
                    lat.postValue(location.latitude)
                    lng.postValue(location.longitude)
                    _markerLocation.postValue(location)
                }
            }
        }
    }

    protected val onDataIOErrorListener = object : DataIOEngine.OnErrorListener {

        override fun onError(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }
    }

    fun unregisterListeners() {
        GlasAI.instance().apply {
            notificationsEngine().unregister(onNotificationsErrorListener)
            dataIO().unregister(onDataAvailableListener, onDataIOErrorListener)
        }
    }
}

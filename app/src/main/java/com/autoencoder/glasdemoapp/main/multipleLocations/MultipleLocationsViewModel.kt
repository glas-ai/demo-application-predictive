package com.autoencoder.glasdemoapp.main.multipleLocations

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
import java.util.*

private const val DESCRIPTION_START_INDEX = 2

enum class MultipleLocationsScreen(@DrawableRes val icon: Int, @StringRes val title: Int) {
    GAS_STATIONS(R.drawable.gas_station, R.string.gas_station_brands),
    SUPERMARKETS(R.drawable.supermarket, R.string.supermarket_brands),
    POINTS_OF_INTEREST(R.drawable.points_of_interest, R.string.points_of_interest)
}

abstract class MultipleLocationsViewModel(multipleLocationScreen: MultipleLocationsScreen) :
    BaseViewModel() {

    protected val _poisMarkers = MutableLiveData<List<PointOfInterest>>()
    protected val _supermarketsMarkers = MutableLiveData<List<Supermarket>>()
    protected val _gasStationsMarkers = MutableLiveData<List<GasStation>>()
    val poisMarkers: LiveData<List<PointOfInterest>> = _poisMarkers
    val supermarketsMarkers: LiveData<List<Supermarket>> = _supermarketsMarkers
    val gasStationsMarkers: LiveData<List<GasStation>> = _gasStationsMarkers

    val icon = MutableLiveData(multipleLocationScreen.icon)
    val title = MutableLiveData(multipleLocationScreen.title)
    val description = MutableLiveData("")

    private val pointsOfInterestResponseType = object : TypeToken<Response<PointsOfInterestData>>() {}.type
    private val supermarketsResponseType = object : TypeToken<Response<SupermarketsData>>() {}.type
    private val gasStationsResponseType = object : TypeToken<Response<GasStationsData>>() {}.type

    abstract fun requestMarkerLocation(location: Location)

    protected val onNotificationsErrorListener = object : NotificationsEngine.OnErrorListener {

        override fun onError(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }
    }

    protected val onDataAvailableListener = object : DataIOEngine.OnDataAvailableListener {

        override fun onDataAvailable(dataJson: String) {
            val pois = gson.fromJson<Response<PointsOfInterestData>>(
                dataJson,
                pointsOfInterestResponseType
            )
            val supermarkets =
                gson.fromJson<Response<SupermarketsData>>(dataJson, supermarketsResponseType)
            val gasStations =
                gson.fromJson<Response<GasStationsData>>(dataJson, gasStationsResponseType)

            if (!pois.reply.data.pointsOfInterests.isNullOrEmpty()) {
                _poisMarkers.postValue(pois.reply.data.pointsOfInterests)
                description.postValue(pois.reply.data.pointsOfInterests.fold("") { description, element ->
                    "$description, ${element.name.capitalize(Locale.ROOT)}"
                }.substring(DESCRIPTION_START_INDEX))
            } else if (!supermarkets.reply.data.supermarkets.isNullOrEmpty()) {
                if (supermarkets.reply.data.supermarkets.isNullOrEmpty()) return

                _supermarketsMarkers.postValue(supermarkets.reply.data.supermarkets)
                description.postValue(supermarkets.reply.data.supermarkets.fold("") { description, element ->
                    "$description, ${element.name.capitalize(Locale.ROOT)}"
                }.substring(DESCRIPTION_START_INDEX))
            } else if (!gasStations.reply.data.gasStations.isNullOrEmpty()) {
                if (gasStations.reply.data.gasStations.isNullOrEmpty()) return

                _gasStationsMarkers.postValue(gasStations.reply.data.gasStations)
                description.postValue(gasStations.reply.data.gasStations.fold("") { description, element ->
                    "$description, ${element.name.capitalize(Locale.ROOT)}"
                }.substring(DESCRIPTION_START_INDEX))
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

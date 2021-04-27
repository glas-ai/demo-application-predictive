package com.autoencoder.glasdemoapp.main.location.home

import com.autoencoder.glasdemoapp.main.location.LocationScreen
import com.autoencoder.glasdemoapp.main.location.LocationViewModel
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.models.DefaultQuery.Companion.getQueryObjectFromEnum
import glas.ai.sdk.GlasAI

class HomeLocationViewModel : LocationViewModel(LocationScreen.HOME) {

    override fun requestMarkerLocation() {
        GlasAI.instance().apply {
            notificationsEngine().register(onNotificationsErrorListener)
            dataIO().register(onDataAvailableListener, onDataIOErrorListener)
            dataIO().queryData(gson.toJson(DefaultQuery.HOME_ADDRESS.getQueryObjectFromEnum()))
        }
    }
}

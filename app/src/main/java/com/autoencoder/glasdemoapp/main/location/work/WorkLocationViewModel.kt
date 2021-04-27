package com.autoencoder.glasdemoapp.main.location.work

import com.autoencoder.glasdemoapp.main.location.LocationScreen
import com.autoencoder.glasdemoapp.main.location.LocationViewModel
import com.autoencoder.glasdemoapp.models.DefaultQuery
import com.autoencoder.glasdemoapp.models.DefaultQuery.Companion.getQueryObjectFromEnum
import glas.ai.sdk.GlasAI

class WorkLocationViewModel : LocationViewModel(LocationScreen.WORK) {

    override fun requestMarkerLocation() {
        GlasAI.instance().apply {
            notificationsEngine().register(onNotificationsErrorListener)
            dataIO().register(onDataAvailableListener, onDataIOErrorListener)
            dataIO().queryData(gson.toJson(DefaultQuery.WORK_ADDRESS.getQueryObjectFromEnum()))
        }
    }
}

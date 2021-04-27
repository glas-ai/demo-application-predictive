package com.autoencoder.glasdemoapp.main.multipleLocations.gasStations

import com.autoencoder.glasdemoapp.main.multipleLocations.MultipleLocationsScreen
import com.autoencoder.glasdemoapp.main.multipleLocations.MultipleLocationsViewModel
import com.autoencoder.glasdemoapp.models.*
import glas.ai.sdk.GlasAI

class GasStationsViewModel: MultipleLocationsViewModel(MultipleLocationsScreen.GAS_STATIONS) {

    override fun requestMarkerLocation(location: Location) {
        GlasAI.instance().apply {
            notificationsEngine().register(onNotificationsErrorListener)
            dataIO().register(onDataAvailableListener, onDataIOErrorListener)
            dataIO().queryData(
                gson.toJson(
                    GasStationsQuery(
                        GasStationsQueryData(
                            GasStations()
                        )
                    )
                )
            )
        }
    }
}

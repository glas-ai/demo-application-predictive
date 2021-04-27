package com.autoencoder.glasdemoapp.main.multipleLocations.pointsOfInterest

import com.autoencoder.glasdemoapp.main.multipleLocations.MultipleLocationsScreen
import com.autoencoder.glasdemoapp.main.multipleLocations.MultipleLocationsViewModel
import com.autoencoder.glasdemoapp.models.*
import glas.ai.sdk.GlasAI

class PointsOfInterestViewModel :
    MultipleLocationsViewModel(MultipleLocationsScreen.POINTS_OF_INTEREST) {

    override fun requestMarkerLocation(location: Location) {
        GlasAI.instance().apply {
            notificationsEngine().register(onNotificationsErrorListener)
            dataIO().register(onDataAvailableListener, onDataIOErrorListener)

            dataIO().queryData(
                gson.toJson(
                    PointsOfInterestQuery(
                        PointsOfInterestQueryData(
                            PointsOfInterest()
                        )
                    )
                )
            )
        }
    }
}

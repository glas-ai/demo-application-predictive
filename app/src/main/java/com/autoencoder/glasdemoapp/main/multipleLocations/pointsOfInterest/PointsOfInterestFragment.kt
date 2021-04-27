package com.autoencoder.glasdemoapp.main.multipleLocations.pointsOfInterest

import com.autoencoder.glasdemoapp.main.multipleLocations.MultipleLocationsFragment
import com.autoencoder.glasdemoapp.main.multipleLocations.PROPERTY_NAME
import com.autoencoder.glasdemoapp.main.multipleLocations.PROPERTY_SELECTED
import com.autoencoder.glasdemoapp.models.Location
import com.autoencoder.glasdemoapp.models.PointOfInterest
import com.autoencoder.glasdemoapp.models.Supermarket
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import org.koin.androidx.viewmodel.ext.android.viewModel

class PointsOfInterestFragment : MultipleLocationsFragment() {

    override val viewModel by viewModel<PointsOfInterestViewModel>()

    override fun onLocationReady(location: Location) {
        viewModel.requestMarkerLocation(location)
    }

    override fun setupObservers() {
        viewModel.poisMarkers.observe(viewLifecycleOwner, ::placeMarkers)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterListeners()
    }

    protected fun placeMarkers(locationsList: List<PointOfInterest>) {
        val featureList = mutableListOf<Feature>()
        locationsList.forEach {
            featureList.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        it.longitude.toDouble(),
                        it.latitude.toDouble()
                    )
                ).apply {
                    addBooleanProperty(PROPERTY_SELECTED, true)
                    addStringProperty(PROPERTY_NAME, it.name)
                }
            )
        }
        featureCollection = FeatureCollection.fromFeatures(featureList)
        setMapStyle()
    }
}



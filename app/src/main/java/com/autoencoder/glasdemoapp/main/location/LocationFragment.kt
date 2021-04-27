package com.autoencoder.glasdemoapp.main.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mapbox.android.core.location.*
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationUpdate
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.databinding.LocationFragmentBinding
import com.autoencoder.glasdemoapp.main.headingInformation.DEFAULT_INTERVAL_IN_MILLISECONDS
import com.autoencoder.glasdemoapp.main.headingInformation.DEFAULT_MAX_WAIT_TIME
import com.autoencoder.glasdemoapp.models.Location
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import com.autoencoder.glasdemoapp.shared.utils.extensions.toast
import com.autoencoder.glasdemoapp.shared.utils.generateBitmap
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.style.layers.CannotAddLayerException
import com.mapbox.mapboxsdk.style.sources.CannotAddSourceException
import com.tbruyelle.rxpermissions2.RxPermissions

const val ZOOM_LEVEL = 13.0
const val MARKER_ZOOM_LEVEL = 14.0

private const val ICON_ID = "ICON_ID_LOCATION"
private const val SOURCE_ID = "SOURCE_ID_LOCATION"
private const val LAYER_ID = "LAYER_ID_LOCATION"
private const val TAG = "LocationFragment"

abstract class LocationFragment : BaseFragment<LocationFragmentBinding>(true) {

    protected var mapView: MapView? = null
    protected var mapboxMap: MapboxMap? = null
    protected lateinit var locationEngine: LocationEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { context ->
            Mapbox.getInstance(context, getString(R.string.mapbox_public_token))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LocationFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.gasStationMap?.let {
            it.onCreate(savedInstanceState)
            mapView = it
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupViews() {
        requestPermission()
    }

    private fun setupMap() {
        mapView?.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle(
                Style.Builder().fromUri(Style.MAPBOX_STREETS)
            ) {
                enableLocationComponent(it)
                setupObservers()
            }
            //             Construct a CameraPosition and animate the camera to that position.
            val cameraPosition: CameraPosition = CameraPosition.Builder()
                .zoom(ZOOM_LEVEL)     // Sets the zoom
                .build()              // Creates a CameraPosition from the builder
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        }
    }

    abstract fun setupObservers()

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RxPermissions(this)
            .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe {
                if (it.granted) {
                    setupMap()
                }
            }
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(style: Style) {
        context?.let { context ->
            mapboxMap?.locationComponent?.apply {
                activateLocationComponent(
                    LocationComponentActivationOptions.builder(context, style)
                        .useDefaultLocationEngine(false)
                        .build()
                )
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING_GPS
                renderMode = RenderMode.COMPASS
            }
            initLocationEngine(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine(context: Context) {
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

        with(LocationEngineProvider.getBestLocationEngine(context)) {
            locationEngine = this
            requestLocationUpdates(request, locationEngineCallback, Looper.getMainLooper())
            getLastLocation(locationEngineCallback)
        }
    }

    protected fun placeMarker(location: Location) {
        val view = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setImageResource(R.drawable.ic_map_marker)
        }
        val symbolLayerIconFeatureList = Feature.fromGeometry(
            Point.fromLngLat(
                location.longitude.toDouble(),
                location.latitude.toDouble()
            )
        )
        mapboxMap?.getStyle {
            if (it.getImage(ICON_ID) == null) {
                it.addImage(ICON_ID, view.generateBitmap())
            }
            if (it.getSource(SOURCE_ID) == null) {
                it.addSource(
                    GeoJsonSource(
                        SOURCE_ID,
                        FeatureCollection.fromFeature(symbolLayerIconFeatureList)
                    )
                )
            }
            if (it.getLayer(LAYER_ID) == null) {
                it.addLayer(
                    SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                            iconImage(ICON_ID),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                        )
                )
            }
        }
        mapboxMap?.apply {
            animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(location.latitude.toDouble(), location.longitude.toDouble()))
                        .build()
                ), 1
            )
        }
    }

    private fun removeMarkers() {
        mapboxMap?.getStyle { style ->
            style.apply {
                removeSource(SOURCE_ID)
                removeLayer(LAYER_ID)
                removeImage(ICON_ID)
            }
        }
    }

    override fun onBack() {
        removeMarkers()
    }

    private val locationEngineCallback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation?.let { lastLocation ->
                mapboxMap?.locationComponent?.apply {
                    zoomWhileTracking(ZOOM_LEVEL, 0)
                    forceLocationUpdate(
                        LocationUpdate.Builder().location(lastLocation).build()
                    )
                }
            }
        }

        override fun onFailure(exception: Exception) {
            toast(exception.localizedMessage ?: exception.stackTraceToString())
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        mapboxMap?.locationComponent?.isLocationComponentEnabled = false
        mapboxMap = null
        mapView?.onDestroy()
    }
}
package com.autoencoder.glasdemoapp.main.multipleLocations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
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
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.databinding.MultipleLocationsFragmentBinding
import com.autoencoder.glasdemoapp.main.headingInformation.DEFAULT_INTERVAL_IN_MILLISECONDS
import com.autoencoder.glasdemoapp.main.headingInformation.DEFAULT_MAX_WAIT_TIME
import com.autoencoder.glasdemoapp.main.location.ZOOM_LEVEL
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
import java.util.*
import kotlin.collections.HashMap

const val MARKER_IMAGE_ID = "MARKER_IMAGE_ID_ML"
const val MARKER_LAYER_ID = "MARKER_LAYER_ID_ML"
const val CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID_ML"
const val PROPERTY_SELECTED = "selected_ml"
const val PROPERTY_NAME = "name_ml"
const val SOURCE_ID = "SOURCE_ID_ML"

private const val TAG = "MultipleLocationFragment"

abstract class MultipleLocationsFragment : BaseFragment<MultipleLocationsFragmentBinding>(true) {

    protected var mapView: MapView? = null
    protected var mapboxMap: MapboxMap? = null
    protected lateinit var featureCollection: FeatureCollection
    protected lateinit var source: GeoJsonSource

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
    ): View = MultipleLocationsFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

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

    abstract fun setupObservers()

    private fun setupMap() {
        mapView?.getMapAsync { mapboxMap ->
            mapboxMap.addOnMapClickListener {
                handleClickIcon(
                    mapboxMap.projection.toScreenLocation(it)
                )
            }
            this.mapboxMap = mapboxMap
            this.mapboxMap?.setStyle(Style.MAPBOX_STREETS) {
                enableLocationComponent(it)
                setupObservers()
            }
            val cameraPosition: CameraPosition = CameraPosition.Builder()
                .zoom(ZOOM_LEVEL)     // Sets the zoom
                .build()              // Creates a CameraPosition from the builder
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1)
        }
    }

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RxPermissions(this)
            .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe {
                if (it.granted) {
                    setupMap()
                    context?.let { context -> initLocationEngine(context, initialLocationCallback) }
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
            initLocationEngine(context, locationEngineCallback)
        }
    }

    @SuppressLint("MissingPermission")
    protected fun initLocationEngine(
        context: Context,
        callback: LocationEngineCallback<LocationEngineResult>
    ) {
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

        with(LocationEngineProvider.getBestLocationEngine(context)) {
            requestLocationUpdates(request, locationEngineCallback, Looper.getMainLooper())
            getLastLocation(callback)
        }
    }

//    protected fun placeMarkers(locationsList: List<PointOfInterest>) {
//        val featureList = mutableListOf<Feature>()
//        locationsList.forEach {
//            featureList.add(
//                Feature.fromGeometry(
//                    Point.fromLngLat(
//                        it.longitude.toDouble(),
//                        it.latitude.toDouble()
//                    )
//                ).apply {
//                    addBooleanProperty(PROPERTY_SELECTED, true)
//                    addStringProperty(PROPERTY_NAME, it.name)
//                }
//            )
//        }
//        featureCollection = FeatureCollection.fromFeatures(featureList)
//        setMapStyle()
//    }

    protected fun setMapStyle() {
        mapboxMap?.getStyle { style ->
            if (style.getSource(SOURCE_ID) == null) {
                style.addSource(GeoJsonSource(SOURCE_ID, featureCollection).also { source = it })
            }
            addMarkerImage(style)
            addMarkerLayer(style)
            addInfoBubbleLayer(style)
            refreshSource()
            enableLocationComponent(style)
        }
    }

    private fun addMarkerImage(style: Style) {
        BitmapUtils.getBitmapFromDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_map_marker, null)
        )?.let { style.addImage(MARKER_IMAGE_ID, it) }
    }

    private fun addMarkerLayer(style: Style) {
        if (style.getLayer(MARKER_LAYER_ID) == null) {

            style.addLayer(
                SymbolLayer(MARKER_LAYER_ID, SOURCE_ID)
                    .withProperties(
                        iconImage(MARKER_IMAGE_ID),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true),
                        iconOffset(arrayOf(0f, -8f))
                    )
            )
        }
    }

    private fun addInfoBubbleLayer(style: Style) {
        if (style.getLayer(CALLOUT_LAYER_ID) == null) {
            style.addLayer(
                SymbolLayer(CALLOUT_LAYER_ID, SOURCE_ID)
                    .withProperties(
                        iconImage("{$PROPERTY_NAME}"),
                        iconAnchor(ICON_ANCHOR_BOTTOM),
                        iconAllowOverlap(true),
                        iconOffset(arrayOf(-2f, -28f))
                    )
                    .withFilter(eq((get(PROPERTY_SELECTED)), literal(true)))
            )
        }
        style.addImages(generateImagesMap(featureCollection))
    }

    private fun generateImagesMap(featureCollection: FeatureCollection): HashMap<String, Bitmap> {
        val imagesMap = HashMap<String, Bitmap>()
        featureCollection.features()?.forEach { feature ->
            val name = feature.getStringProperty(PROPERTY_NAME)
            val bubbleView =
                LayoutInflater.from(context).inflate(R.layout.view_info_bubble, null, false)
                    ?.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        findViewById<TextView>(R.id.text).text = name.capitalize(Locale.ROOT)
                    }
            bubbleView?.let { view ->
                imagesMap[name] = view.generateBitmap()
            }
        }
        return imagesMap
    }

    private fun handleClickIcon(screenPoint: PointF): Boolean {
        val features =
            mapboxMap?.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID) ?: return false
        if (features.isEmpty())
            return false
        featureCollection.features()?.forEach {
            if (it.getStringProperty(PROPERTY_NAME) == features[0].getStringProperty(PROPERTY_NAME)) {
                if (it.selectStatus()) {
                    it.setSelectState(false)
                } else {
                    it.setSelected()
                }
            }
        }
        return true
    }

    private fun Feature.setSelected() {
        setSelectState(true)
        refreshSource()
    }

    private fun Feature.setSelectState(selectedState: Boolean) {
        properties()?.addProperty(PROPERTY_SELECTED, selectedState)
        refreshSource()
    }

    private fun refreshSource() {
        source.setGeoJson(featureCollection)
    }

    private fun Feature.selectStatus() = getBooleanProperty(PROPERTY_SELECTED) ?: false

    private fun removeMarkers() {
        mapboxMap?.getStyle { style ->
            style.apply {
                removeSource(SOURCE_ID)
                removeLayer(CALLOUT_LAYER_ID)
                removeLayer(MARKER_LAYER_ID)
                removeImage(MARKER_IMAGE_ID)
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
                    forceLocationUpdate(
                        LocationUpdate.Builder()
                            .location(lastLocation)
                            .build()
                    )
                }
            }
        }

        override fun onFailure(exception: Exception) {
            toast(exception.localizedMessage ?: exception.stackTraceToString())
        }
    }

    private val initialLocationCallback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation?.let { lastLocation ->
                onLocationReady(
                    Location(
                        lastLocation.latitude.toFloat(),
                        lastLocation.longitude.toFloat()
                    )
                )
            }
        }

        override fun onFailure(exception: Exception) {
            toast(exception.localizedMessage ?: exception.stackTraceToString())
        }
    }

    abstract fun onLocationReady(location: Location)

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

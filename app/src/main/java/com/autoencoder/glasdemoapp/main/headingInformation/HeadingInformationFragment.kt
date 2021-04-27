package com.autoencoder.glasdemoapp.main.headingInformation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.databinding.HeadingInformationFragmentBinding
import com.autoencoder.glasdemoapp.main.headingInformation.adapter.HeadingInformationAdapter
import com.autoencoder.glasdemoapp.main.location.ZOOM_LEVEL
import com.autoencoder.glasdemoapp.models.HeadingInformation
import com.autoencoder.glasdemoapp.models.HeadingInformationItem
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import com.autoencoder.glasdemoapp.shared.utils.bindingAdapters.HEADING_ONE
import com.autoencoder.glasdemoapp.shared.utils.bindingAdapters.HEADING_THREE
import com.autoencoder.glasdemoapp.shared.utils.bindingAdapters.HEADING_TWO
import com.autoencoder.glasdemoapp.shared.utils.extensions.toast
import com.autoencoder.glasdemoapp.shared.utils.generateBitmap
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.android.core.location.*
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.LocationUpdate
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.mapbox.mapboxsdk.utils.ColorUtils.colorToRgbaString
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
const val LINE_WIDTH = 5.0f

private const val MARKER_IMAGE_ID = "MARKER_IMAGE_ID_HEADING"
private const val MARKER_LAYER_ID = "MARKER_LAYER_ID_HEADING"
private const val CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID_HEADING"
private const val PROPERTY_SELECTED = "selected_heading"
private const val PROPERTY_NAME = "name_heading"
private const val SOURCE_ID = "SOURCE_ID_HEADING"
private const val MINIMUM_DISTANCE_METERS = 1f
private const val UPDATE_INTERVAL = 2_000L
private const val FASTEST_UPDATE_INTERVAL = 1_500L

class HeadingInformationFragment : BaseFragment<HeadingInformationFragmentBinding>(true) {

    override val viewModel by viewModel<HeadingInformationViewModel>()

    private val adapter = HeadingInformationAdapter()
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private lateinit var markerManager: MarkerViewManager
    private lateinit var lineManager: LineManager
    private var featureCollection: FeatureCollection =
        FeatureCollection.fromFeatures(mutableListOf<Feature>())
    private lateinit var source: GeoJsonSource
    private var lastPosition: Location? = null

    private var isInit = false


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
    ): View = HeadingInformationFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.headingInformationMap?.let {
            it.onCreate(savedInstanceState)
            mapView = it
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupViews() {
        binding?.headingInformation?.adapter = adapter
        setupObservers()
        requestPermission()
    }

    private fun setupMap() {
        mapView?.getMapAsync { mapboxMap ->
            markerManager = MarkerViewManager(mapView, mapboxMap)
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                mapView?.let { mapView ->
                    lineManager = LineManager(mapView, mapboxMap, it)
                }
                mapboxMap.addOnMapClickListener { latLng ->
                    handleClickIcon(
                        mapboxMap.projection.toScreenLocation(latLng)
                    )
                }

                viewModel.requestHeadingInformation()
                enableLocationComponent(it)
            }

//             Construct a CameraPosition and animate the camera to that position.
            val cameraPosition: CameraPosition = CameraPosition.Builder()
                .tilt(60.0)        // Sets the tilt of the camera to 60 degrees
                .build()              // Creates a CameraPosition from the builder
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            this.mapboxMap = mapboxMap
        }

    }

    private fun setupObservers() {
        viewModel.headingItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            placeMarkers(it)
        }
        viewModel.headingInformation.observe(viewLifecycleOwner, ::tracePolylines)
    }

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
                val locationComponentOptions = LocationComponentOptions.builder(context)
                    .bearingDrawable(R.drawable.transparent_circle)
                    .backgroundDrawable(R.drawable.transparent_circle)
                    .foregroundDrawable(R.drawable.ic_heading_direction)
                    .elevation(0f)
                    .accuracyAnimationEnabled(false)
                    .accuracyAlpha(0f)
                    .build()
                activateLocationComponent(
                    LocationComponentActivationOptions.builder(context, style)
                        .useDefaultLocationEngine(false)
                        .locationComponentOptions(locationComponentOptions)
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
            requestLocationUpdates(request, locationEngineCallback, getMainLooper())
            getLastLocation(locationEngineCallback)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        context?.let { context ->
            val request = LocationRequest.create().apply {
                interval = UPDATE_INTERVAL
                fastestInterval = FASTEST_UPDATE_INTERVAL
                priority = PRIORITY_HIGH_ACCURACY
            }
            LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(request, locationUpdateListener, Looper.getMainLooper())
        }
    }

    private fun placeMarkers(locationsList: List<HeadingInformationItem>) {
        val featureList = mutableListOf<Feature>()
        locationsList.forEach {
            featureList.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        it.lng.toDouble(),
                        it.lat.toDouble()
                    )
                ).apply {
                    addBooleanProperty(PROPERTY_SELECTED, true)
                    val markerProbability = "${(it.probability * 100).toInt()}%"
                    val markerText = "${it.tag.capitalize(Locale.ROOT)} : $markerProbability"
                    addStringProperty(PROPERTY_NAME, markerText)
                }
            )
        }
        featureCollection = FeatureCollection.fromFeatures(featureList)
        customizeMapStyle()
    }

    private fun customizeMapStyle() {
        mapboxMap?.getStyle { style ->
            style.removeSource(SOURCE_ID)
            if (style.getSource(SOURCE_ID) == null) {
                style.addSource(GeoJsonSource(SOURCE_ID, featureCollection).also { source = it })
            }
            addMarkerImage(style)
            addMarkerLayer(style)
            addInfoBubbleLayer(style)
            refreshSource()
        }
    }

    private fun addMarkerImage(style: Style) {
        BitmapUtils.getBitmapFromDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_map_marker, null)
        )?.let {
            if (style.getImage(MARKER_IMAGE_ID) == null) {
                style.addImage(MARKER_IMAGE_ID, it)
            }
        }
    }

    private fun addMarkerLayer(style: Style) {
        if (style.getLayer(MARKER_LAYER_ID) == null) {
            style.addLayer(
                SymbolLayer(MARKER_LAYER_ID, SOURCE_ID)
                    .withProperties(
                        PropertyFactory.iconImage(MARKER_IMAGE_ID),
                        PropertyFactory.iconAllowOverlap(true),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconOffset(arrayOf(0f, -8f))
                    )
            )
        }
    }

    private fun addInfoBubbleLayer(style: Style) {
        if (style.getLayer(CALLOUT_LAYER_ID) == null) {
            style.addLayer(
                SymbolLayer(CALLOUT_LAYER_ID, SOURCE_ID)
                    .withProperties(
                        PropertyFactory.iconImage("{$PROPERTY_NAME}"),
                        PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                        PropertyFactory.iconAllowOverlap(true),
                        PropertyFactory.iconOffset(arrayOf(-2f, -28f))
                    )
                    .withFilter(
                        Expression.eq(
                            (Expression.get(PROPERTY_SELECTED)),
                            Expression.literal(true)
                        )
                    )
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


    private fun tracePolylines(headingInformation: List<HeadingInformation>) {
        lineManager.deleteAll()

        context?.let { context ->
            headingInformation.forEachReversedWithIndex { index, headingInformation ->
                val latLngs =
                    headingInformation.route.legs.firstOrNull()?.waypoints?.map { waypoint ->
                        LatLng(
                            waypoint.location.latitude.toDouble(),
                            waypoint.location.longitude.toDouble()
                        )
                    }
                val color = ContextCompat.getColor(
                    context, when (index) {
                        HEADING_ONE -> R.color.green_data
                        HEADING_TWO -> R.color.blue_data
                        HEADING_THREE -> R.color.orange_data
                        else -> R.color.orange_data
                    }
                )

                lineManager.create(
                    LineOptions()
                        .withLatLngs(latLngs)
                        .withLineColor(colorToRgbaString(color))
                        .withLineWidth(LINE_WIDTH)
                )
            }
        }

        refreshSource()
    }

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
                    zoomWhileTracking(ZOOM_LEVEL, 0)
                    forceLocationUpdate(
                        LocationUpdate.Builder().location(lastLocation).build()
                    )
                    lastPosition = lastLocation
                    startLocationUpdates()
                }
            }
        }

        override fun onFailure(exception: Exception) {
            toast(exception.localizedMessage ?: exception.stackTraceToString())
        }
    }

    private val locationUpdateListener =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val currentLocation = locationResult.lastLocation
                val bearing = currentLocation.bearing
                val cameraPosition = CameraPosition.Builder()
                if (lastPosition?.distanceTo(currentLocation) ?: 0f > MINIMUM_DISTANCE_METERS) {
                    cameraPosition
                        .bearing(bearing.toDouble())
                        .target(LatLng(currentLocation))
                }
                mapboxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))

            }
        }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestHeadingInformation()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.unregisterListeners()
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

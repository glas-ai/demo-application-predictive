package com.autoencoder.glasdemoapp.main.location.home

import com.autoencoder.glasdemoapp.main.location.LocationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeLocationFragment : LocationFragment() {

    override val viewModel by viewModel<HomeLocationViewModel>()

    override fun setupViews() {
        super.setupViews()
        viewModel.requestMarkerLocation()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterListeners()
    }

    override fun setupObservers() {
        viewModel.markerLocation.observe(viewLifecycleOwner, ::placeMarker)
    }
}
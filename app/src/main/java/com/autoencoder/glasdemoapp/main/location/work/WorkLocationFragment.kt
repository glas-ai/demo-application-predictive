package com.autoencoder.glasdemoapp.main.location.work

import com.autoencoder.glasdemoapp.main.location.LocationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class WorkLocationFragment : LocationFragment() {

    override val viewModel by viewModel<WorkLocationViewModel>()

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
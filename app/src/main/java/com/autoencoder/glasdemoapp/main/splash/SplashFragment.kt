package com.autoencoder.glasdemoapp.main.splash

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.databinding.SplashFragmentBinding
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import com.autoencoder.glasdemoapp.shared.utils.extensions.showDialog
import com.autoencoder.glasdemoapp.shared.utils.extensions.toast
import com.tbruyelle.rxpermissions2.RxPermissions
import glas.ai.sdk.GlasAI
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REQ_SOLVE_SERVICES = 2404

class SplashFragment : BaseFragment<SplashFragmentBinding>() {

    override val viewModel by viewModel<SplashViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = SplashFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root


    override fun setupViews() {
        setupObservers()
        hasPlayServices()
    }

    private fun setupObservers() {
        viewModel.cmd.observe(viewLifecycleOwner) { command ->
            when (command) {
                is SplashViewModel.Command.ShowRequestedPermissionsDialog ->
                    showDialog(
                        getString(R.string.missing_permissions),
                        getString(R.string.missing_permissions_info),
                        getString(R.string.ok),
                        { activity?.finish() }
                    )
            }
        }
    }

    private fun hasPlayServices() {
        activity?.let { context ->
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
            if (status != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(status)) {
                    googleApiAvailability.getErrorDialog(context, status, REQ_SOLVE_SERVICES)?.show()
                } else {
                    toast(R.string.play_services_error)
                }
            }
            requestPermissions()
        }
    }

    @SuppressLint("CheckResult")
    private fun requestPermissions() {
        RxPermissions(this)
            .requestEachCombined(
                *GlasAI.instance().requiredPermissions().toMutableSet().also {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        it.add(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                }.toTypedArray()
            )
            .subscribe {
                if (it.granted) {
                    viewModel.initializeGlas()
                } else {
                    viewModel.displayPermissionsRefusedDialog()
                }
            }
    }
}

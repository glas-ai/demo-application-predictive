package com.autoencoder.glasdemoapp.main.list

import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.application.NotificationProvider
import com.autoencoder.glasdemoapp.databinding.ListFragmentBinding
import com.autoencoder.glasdemoapp.main.about.AboutDialog
import com.autoencoder.glasdemoapp.main.list.adapter.DemoActivitiesAdapter
import com.autoencoder.glasdemoapp.models.DemoActivityItem
import com.autoencoder.glasdemoapp.models.FeatureRequirement
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import com.autoencoder.glasdemoapp.shared.utils.extensions.showCriteriaDialog
import com.autoencoder.glasdemoapp.shared.utils.extensions.showTurnOnDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val GPS_REQUEST_CODE = 1001
private const val INTERNET_REQUEST_CODE = 1002

class ListFragment : BaseFragment<ListFragmentBinding>() {

    private val navArgs by navArgs<ListFragmentArgs>()

    override val viewModel by viewModel<ListViewModel>()

    private val adapter: DemoActivitiesAdapter by lazy {
        DemoActivitiesAdapter(viewModel::onActivityClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ListFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun setupViews() {
        binding?.demoActivities?.adapter = adapter
        setupObservers()
        viewModel.registerGlasAIListeners()
        viewModel.loadActivities(navArgs.items)
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.queryAvailableDataTypes()
        }
    }

    private fun setupObservers() {
        viewModel.activities.observe(viewLifecycleOwner, adapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) { command ->
            when (command) {
                is ListViewModel.Command.ShowDialog -> showCriteriaDialog(command.service)
                is ListViewModel.Command.CheckGPS -> checkGPSEnabled(command.feature)
                is ListViewModel.Command.ShowInternetDialog -> showTurnOnDialog(
                    command.feature,
                    FeatureRequirement.INTERNET
                ) {
                    startActivityForResult(
                        Intent(Settings.ACTION_DATA_ROAMING_SETTINGS),
                        INTERNET_REQUEST_CODE
                    )
                }
                is ListViewModel.Command.StopRefreshing ->
                    binding?.swipeRefresh?.isRefreshing = false
                is ListViewModel.Command.ShowAboutDialog ->
                    AboutDialog().show(childFragmentManager, AboutDialog::class.java.name)
            }
        }
        viewModel.notification.observe(viewLifecycleOwner) { notifyIfGPSDisabled() }
    }

    private fun checkGPSEnabled(feature: DemoActivityItem) {
        context?.let { context ->
            (context.getSystemService(LOCATION_SERVICE) as? LocationManager)?.let { locationManager ->
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    viewModel.openActivity(feature)
                else showTurnOnDialog(feature, FeatureRequirement.GPS) {
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        GPS_REQUEST_CODE
                    )
                }
            }
        }
    }

    private fun notifyIfGPSDisabled() {
        context?.let { context ->
            (context.getSystemService(LOCATION_SERVICE) as? LocationManager)?.let { locationManager ->
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                ) {
                    NotificationProvider.createNotification(context)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterListeners()
    }
}

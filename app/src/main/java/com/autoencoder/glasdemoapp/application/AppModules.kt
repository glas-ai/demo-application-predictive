package com.autoencoder.glasdemoapp.application

import com.autoencoder.glasdemoapp.main.MainActivityViewModel
import com.autoencoder.glasdemoapp.main.about.glas.AboutGlasViewModel
import com.autoencoder.glasdemoapp.main.about.qt.QTLicenseViewModel
import com.autoencoder.glasdemoapp.main.multipleLocations.gasStations.GasStationsViewModel
import com.autoencoder.glasdemoapp.main.headingInformation.HeadingInformationViewModel
import com.autoencoder.glasdemoapp.main.list.ListViewModel
import com.autoencoder.glasdemoapp.main.location.home.HomeLocationViewModel
import com.autoencoder.glasdemoapp.main.location.work.WorkLocationViewModel
import com.autoencoder.glasdemoapp.main.multipleLocations.pointsOfInterest.PointsOfInterestViewModel
import com.autoencoder.glasdemoapp.main.multipleLocations.supermarkets.SupermarketsViewModel
import com.autoencoder.glasdemoapp.main.splash.SplashViewModel
import com.autoencoder.glasdemoapp.main.userSchedule.UserDailyScheduleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModules {

    private val viewModels = module {
        viewModel { MainActivityViewModel() }
        viewModel { SplashViewModel() }
        viewModel { ListViewModel() }
        viewModel { UserDailyScheduleViewModel() }
        viewModel { HeadingInformationViewModel() }
        viewModel { PointsOfInterestViewModel() }
        viewModel { GasStationsViewModel() }
        viewModel { SupermarketsViewModel() }
        viewModel { HomeLocationViewModel() }
        viewModel { WorkLocationViewModel() }
        viewModel { AboutGlasViewModel() }
        viewModel { QTLicenseViewModel() }
    }

    val modules = listOf(viewModels)

}
package com.autoencoder.glasdemoapp.application

import android.app.Application
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.shared.utils.extensions.saveToTemporaryFile
import com.autoencoder.glasdemoapp.shared.utils.persistence.PersistenceService
import com.google.firebase.FirebaseApp
import com.orhanobut.hawk.Hawk
import glas.ai.sdk.GlasAI
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

private const val PROFILE_FILE_NAME = "glas_profile"

class GlasDemoApp : Application(), PersistenceService {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        Hawk.init(applicationContext).build()
        startKoin {
            androidContext(this@GlasDemoApp)
            modules(AppModules.modules)
        }


        val config = GlasAI.Config(applicationContext)
            .notificationInfo(
                R.mipmap.ic_launcher,
                getString(R.string.app_name),
                getString(R.string.subtitle)
            )
            .autoSaveProfile(true)

        GlasAI.init(config)
        NotificationProvider.createNotificationChannel(this)
    }

    private fun readProfileAndSaveToTempFile() =
        applicationContext.resources.assets.open(PROFILE_FILE_NAME).readBytes()
            .saveToTemporaryFile(applicationContext)
}

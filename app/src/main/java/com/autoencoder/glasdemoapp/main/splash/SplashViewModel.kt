package com.autoencoder.glasdemoapp.main.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import com.autoencoder.glasdemoapp.shared.utils.LiveEvent
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY = 500L

class SplashViewModel : BaseViewModel() {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    fun displayPermissionsRefusedDialog() {
        _cmd.value = Command.ShowRequestedPermissionsDialog
    }

    fun initializeGlas() {
        GlasAI.instance().apply {
            dataIO().register(onAvailableDataTypesListener, onDataIOErrorListener)
            registerOnReadyListener(glasOnReadyListener)
            boot()
        }
    }

    private val onAvailableDataTypesListener = object : DataIOEngine.OnAvailableDataTypesListener {

        override fun onAvailableDataTypes(metaDataJson: Map<Byte, String>) {
            metaDataJson[DataIOEngine.OUTPUT]?.let {
                navigateToMainScreen(it)
            }
        }
    }

    private val onDataIOErrorListener = object : DataIOEngine.OnErrorListener {

        override fun onError(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }
    }

    private val glasOnReadyListener = GlasAI.OnReadyListener {
        GlasAI.instance().dataIO().queryAvailableDataTypes(DataIOEngine.INPUT_OUTPUT)
    }

    private fun navigateToMainScreen(items: String) {
        viewModelScope.launch {
            delay(DELAY)
            _baseCmd.postValue(
                BaseCommand.Navigate(
                    SplashFragmentDirections.actionSplashFragmentToListFragment(items)
                )
            )
        }
    }

    sealed class Command {
        object ShowRequestedPermissionsDialog : Command()
    }
}

package com.autoencoder.glasdemoapp.shared.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.google.gson.Gson
import com.autoencoder.glasdemoapp.shared.utils.LiveEvent

abstract class BaseViewModel: ViewModel() {

    @SuppressWarnings("VariableNaming")
    protected val _baseCmd = LiveEvent<BaseCommand>()
    val baseCmd:  LiveData<BaseCommand> = _baseCmd

    protected val gson = Gson()

    fun onBack() {
        _baseCmd.value = BaseCommand.NavigateUp
    }
}

sealed class BaseCommand {
    data class ShowToast(val message: String) : BaseCommand()
    data class ShowToastById(@StringRes val messageId: Int) : BaseCommand()
    data class Navigate(val direction: NavDirections) : BaseCommand()
    object NavigateUp : BaseCommand()
}

package com.autoencoder.glasdemoapp.main.headingInformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import glas.ai.sdk.GlasAI
import glas.ai.sdk.NotificationsEngine
import java.util.*

private const val HEADING_SUB_TYPE = "heading"

class HeadingInformationViewModel : BaseViewModel() {

    private val _headingItems = MutableLiveData<List<HeadingInformationItem>>(listOf())
    val headingItems: LiveData<List<HeadingInformationItem>> = _headingItems

    private val _headingInformation = MutableLiveData<List<HeadingInformation>>()
    val headingInformation: LiveData<List<HeadingInformation>> = _headingInformation

    private val responseType =
        object : TypeToken<NotificationResponse<HeadingInformation>>() {}.type

    private val onNotificationAvailableListener = object : NotificationsEngine.OnNotificationAvailableListener {

        override fun onNotificationAvailable(notificationJson: String) {

            val result = gson.fromJson<NotificationResponse<HeadingInformation>>(
                notificationJson,
                responseType
            )
            with(result.notification.data) {
                setHeadingItems(result.notification.data)
                _headingInformation.postValue(result.notification.data)
            }
        }
    }

    private val onErrorListener = object : NotificationsEngine.OnErrorListener {

        override fun onError(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }
    }

    fun requestHeadingInformation() {
        GlasAI.instance().notificationsEngine().register(onNotificationAvailableListener, onErrorListener)
        GlasAI.instance().notificationsEngine().subscribe(HEADING_SUB_TYPE)
    }

    private fun setHeadingItems(data: List<HeadingInformation>) {
        val sortedHeadingItems = data.sortedBy { it.probability }.reversed()
        val headingItemsList = mutableListOf<HeadingInformationItem>()
        sortedHeadingItems.forEachIndexed { index, headingInformation ->
            val lastWayPointLocation =
                headingInformation.route.legs.firstOrNull()?.waypoints?.lastOrNull()?.location
            headingItemsList.add(
                HeadingInformationItem(
                    index,
                    headingInformation.tag.capitalize(Locale.getDefault()),
                    lastWayPointLocation?.latitude ?: 0f,
                    lastWayPointLocation?.longitude ?: 0f,
                    headingInformation.tag.capitalize(Locale.getDefault()),
                    headingInformation.probability
                )
            )
        }
        _headingItems.postValue(headingItemsList)
    }

    fun unregisterListeners() {
        GlasAI.instance().apply {
            notificationsEngine().unregister(onNotificationAvailableListener, onErrorListener)
        }
    }
}

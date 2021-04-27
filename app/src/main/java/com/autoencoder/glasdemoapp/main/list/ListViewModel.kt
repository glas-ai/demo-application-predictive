package com.autoencoder.glasdemoapp.main.list

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.models.Service.*
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import com.autoencoder.glasdemoapp.shared.utils.LiveEvent
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

private const val FULL_PERCENTAGE = 100

class ListViewModel : BaseViewModel() {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val responseType = object : TypeToken<Response<List<ServiceAvailability>>>() {}.type

    private val _activities = MutableLiveData<List<DemoActivityItem>>()
    val activities: LiveData<List<DemoActivityItem>> = _activities

    private val _notification = LiveEvent<Boolean>()
    val notification: LiveData<Boolean> = _notification

    fun onActivityClicked(activityItem: DemoActivityItem) {
        if (activityItem.percentage < FULL_PERCENTAGE) {
            _cmd.value = Command.ShowDialog(activityItem)
            return
        }

        when (activityItem.service) {
            USER_DAILY_SCHEDULE -> _cmd.value = Command.CheckGPS(activityItem)
            else -> checkInternetAvailable {
                _cmd.value = if (it) Command.CheckGPS(activityItem) else Command.ShowInternetDialog(
                    activityItem
                )
            }
        }
    }

    fun openActivity(activityItem: DemoActivityItem) {
        _baseCmd.value = BaseCommand.Navigate(
            when (activityItem.service.title) {
                R.string.user_daily_schedule -> ListFragmentDirections.actionListFragmentToUserDailyScheduleFragment()
                R.string.heading_information -> ListFragmentDirections.actionListFragmentToHeadingInformationFragment()
                R.string.points_of_interest -> ListFragmentDirections.actionListFragmentToPointsOfInterestFragment()
                R.string.gas_station_brands -> ListFragmentDirections.actionListFragmentToGasStationsFragment()
                R.string.supermarket_brands -> ListFragmentDirections.actionListFragmentToSupermarketsFragment()
                R.string.work_location -> ListFragmentDirections.actionListFragmentToWorkLocationFragment()
                R.string.home_location -> ListFragmentDirections.actionListFragmentToHomeLocationFragment()
                else -> ListFragmentDirections.actionListFragmentToUserDailyScheduleFragment()
            }
        )
    }

    fun registerGlasAIListeners() {
        GlasAI.instance().apply {
            dataIO().register(onAvailableDataTypesListener, onErrorListener)
        }
    }

    fun queryAvailableDataTypes() {
        GlasAI.instance().dataIO().queryAvailableDataTypes(DataIOEngine.INPUT_OUTPUT)
    }

    private val onAvailableDataTypesListener = object : DataIOEngine.OnAvailableDataTypesListener {
        override fun onAvailableDataTypes(metaDataJson: Map<Byte, String>) {
            metaDataJson[DataIOEngine.OUTPUT]?.let {
                serviceAvailability = it
                loadActivities(it)
            }
        }
    }

    private val onErrorListener = object : DataIOEngine.OnErrorListener {
        override fun onError(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }
    }

    fun loadActivities(responseString: String) {
        val result = gson.fromJson<Response<List<ServiceAvailability>>>(
            responseString,
            responseType
        )
        val unsortedActivities = result.reply.data.mapNotNull { service ->
            Service.getServiceFromType(service.type)?.let { serviceType ->
                DemoActivityItem(
                    serviceType,
                    service.availability?.toIntOrNull() ?: 0
                )
            }
        }
        if (unsortedActivities.isEmpty())
            notifyIfNotActivityNotReady()
        unsortedActivities.find { it.percentage < FULL_PERCENTAGE }?.let {
            notifyIfNotActivityNotReady()
        }
        sortActivities(unsortedActivities)
    }

    private fun notifyIfNotActivityNotReady() {
        if (notification.value != false)
            _notification.postValue(true)
    }

    private fun checkInternetAvailable(callback: (isInternet: Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                withContext(Dispatchers.IO) {
                    socket.connect(socketAddress, timeoutMs)
                    socket.close()
                }
                callback(true)
            } catch (e: IOException) {
                callback(false)
            }
        }
    }

    private fun sortActivities(unsortedActivities: List<DemoActivityItem>) {
        val sortedActivities = mutableListOf<DemoActivityItem?>()
        sortedActivities.add(unsortedActivities.find { it.service == HOME_LOCATION }
            ?: DemoActivityItem(HOME_LOCATION, 0))
        sortedActivities.add(unsortedActivities.find { it.service == WORK_LOCATION }
            ?: DemoActivityItem(WORK_LOCATION, 0))
        sortedActivities.add(unsortedActivities.find { it.service == HEADING_INFORMATION }
            ?: DemoActivityItem(HEADING_INFORMATION, 0))
        sortedActivities.add(unsortedActivities.find { it.service == POINTS_OF_INTEREST }
            ?: DemoActivityItem(POINTS_OF_INTEREST, 0))
        sortedActivities.add(unsortedActivities.find { it.service == GAS_STATIONS }
            ?: DemoActivityItem(GAS_STATIONS, 0))
        sortedActivities.add(unsortedActivities.find { it.service == SUPERMARKETS }
            ?: DemoActivityItem(SUPERMARKETS, 0))
        sortedActivities.add(unsortedActivities.find { it.service == USER_DAILY_SCHEDULE }
            ?: DemoActivityItem(USER_DAILY_SCHEDULE, 0))
        _activities.postValue(sortedActivities.filterNotNull())
        _cmd.postValue(Command.StopRefreshing)
    }

    fun unregisterListeners() {
        GlasAI.instance().apply {
            dataIO().unregister(onAvailableDataTypesListener, onErrorListener)
        }
    }

    fun openAboutDialog() {
        _cmd.value = Command.ShowAboutDialog
    }

    private var serviceAvailability = "{\n" +
            "    \"api_info\": {\n" +
            "        \"type\": \"lite_machine_readable\",\n" +
            "        \"version\": \"1.2.0\"\n" +
            "    },\n" +
            "    \"message_id\": \"{6f6ec905-3ad2-4bf2-912d-8788376b772a}\",\n" +
            "    \"reply\": {\n" +
            "        \"data\": [\n" +
            "            {\n" +
            "                \"availability\": 0,\n" +
            "                \"type\": \"favourite-P.O.I.\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"favourite-gasstation\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"favourite-supermarket\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 0,\n" +
            "                \"type\": \"heading\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 0,\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"home-address\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 0,\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"leave-home-time\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"availability\": 0,\n" +
            "                \"state\": \"processed\",\n" +
            "                \"type\": \"work-address\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}"


    sealed class Command {
        data class ShowDialog(val service: DemoActivityItem) : Command()
        data class CheckGPS(val feature: DemoActivityItem) : Command()
        data class ShowInternetDialog(val feature: DemoActivityItem) : Command()
        object StopRefreshing : Command()
        object ShowAboutDialog : Command()
    }
}

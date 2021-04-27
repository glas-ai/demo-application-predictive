package com.autoencoder.glasdemoapp.main.userSchedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.autoencoder.glasdemoapp.models.*
import com.autoencoder.glasdemoapp.models.DefaultQuery.Companion.getQueryObjectFromEnum
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import com.autoencoder.glasdemoapp.shared.utils.extensions.convertToHour
import glas.ai.sdk.DataIOEngine
import glas.ai.sdk.GlasAI

class UserDailyScheduleViewModel : BaseViewModel() {

    private val _scheduleItems = MutableLiveData<List<UserDailyScheduleItem>>()
    val scheduleItem: LiveData<List<UserDailyScheduleItem>> = _scheduleItems

    private val responseType = object : TypeToken<Response<TimeData>>() {}.type

    fun getUserDailySchedule() {
        GlasAI.instance().apply {
            dataIO().register(onDataAvailableListener, onDataIOErrorListener)
            dataIO().queryData(gson.toJson(DefaultQuery.LEAVE_HOME_TIME.getQueryObjectFromEnum()))
        }
    }

    private val onDataAvailableListener = object : DataIOEngine.OnDataAvailableListener {

        override fun onDataAvailable(dataJson: String) {
            val items = mutableListOf<UserDailyScheduleItem>()
            gson.fromJson<Response<TimeData>>(dataJson, responseType)?.also {
                it.reply.data.timeData?.forEach { timeData ->
                    items.add(
                        UserDailyScheduleItem(
                            Day.getDayFromString(
                                timeData.day
                            ),
                            timeData.probability,
                            timeData.time.convertToHour()
                        )
                    )
                }
            }
            _scheduleItems.postValue(items.groupBy { it.day }.values.flatten())
        }
    }

    private val onDataIOErrorListener = object : DataIOEngine.OnErrorListener {

        override fun onError(errorJson: String) {
            _baseCmd.postValue(BaseCommand.ShowToast(errorJson))
        }
    }

    fun unregisterListeners() {
        GlasAI.instance().apply {
            dataIO().unregister(onDataAvailableListener, onDataIOErrorListener)
        }
    }
}

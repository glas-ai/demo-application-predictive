package com.autoencoder.glasdemoapp.models

enum class DefaultQuery(val messageId: String, val dataName: String) {
    LEAVE_HOME_TIME("LEAVE-HOME-TIMES-ID", "leave-home-time"),
    HOME_ADDRESS("HOME-ADDRESS-ID", "home-address"),
    WORK_ADDRESS("WORK-ADDRESS-ID", "work-address"),
    ROAD_SEGMENTS("TRAVELED-ROAD-SEGMENTS-ID", "traveled-road-segments");

    companion object {
        fun DefaultQuery.getQueryObjectFromEnum() =
            Query(this.messageId, QueryData(mapOf(Pair(this.dataName, EmptyClass()))))
    }
}

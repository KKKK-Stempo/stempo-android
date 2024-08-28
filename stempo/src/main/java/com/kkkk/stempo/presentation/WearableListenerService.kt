package com.kkkk.stempo.presentation

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import timber.log.Timber

class WearableListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == PATH_BPM) {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    dataMap.getString(KEY_BPM)?.let {
                        Timber.tag("okhttp").d("SERVICE DATA RECEIVED : $it")
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_BPM = "KEY_BPM"

        const val PATH_BPM = "/bpm"
    }
}
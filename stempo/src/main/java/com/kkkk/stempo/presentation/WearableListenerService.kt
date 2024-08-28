package com.kkkk.stempo.presentation

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import timber.log.Timber

class WearableListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Timber.tag("okhttp").d("SERVICE ON DATA CHANGED")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo(PATH_BPM) == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            val bpm = getInt(KEY_BPM)
                            Timber.tag("okhttp").d("SERVICE DATA RECEIVED : $bpm")
                        }
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
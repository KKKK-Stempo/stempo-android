package com.kkkk.presentation.manager

import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearableDataManager @Inject constructor(
    private val dataClient: DataClient
) {

    fun sendIntToWearable(path: String, key: String, value: Int): Task<DataItem> {

        Timber.tag("okhttp").d("START SENDING DATA TO WEARABLE")

        val putDataReq: PutDataRequest = PutDataMapRequest.create(path).run {
            dataMap.putInt(key, value)
            asPutDataRequest().setUrgent()
        }

        return dataClient.putDataItem(putDataReq).addOnSuccessListener { dataItem ->
            Timber.tag("okhttp").d("SEND DATA TO WEARABLE SUCCESS : ${dataItem.uri}")
        }.addOnFailureListener { exception ->
            Timber.tag("okhttp").d("SEND DATA TO WEARABLE FAIL : ${exception.message}")
        }
    }

    companion object {
        const val KEY_BPM = "KEY_BPM"

        const val PATH_BPM = "/bpm"
    }
}
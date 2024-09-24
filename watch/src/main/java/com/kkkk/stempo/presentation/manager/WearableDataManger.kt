package com.kkkk.stempo.presentation.manager

import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import timber.log.Timber

class WearableDataManager(private val dataClient: DataClient) {

    fun sendDoubleToPhone(path: String, key: String, value: Double): Task<DataItem> {

        Timber.tag("okhttp").d("START SENDING DATA TO PHONE")

        val putDataReq: PutDataRequest = PutDataMapRequest.create(path).run {
            dataMap.putDouble(key, value)
            asPutDataRequest().setUrgent()
        }

        return dataClient.putDataItem(putDataReq).addOnSuccessListener { dataItem ->
            Timber.tag("okhttp").d("SEND DATA TO PHONE SUCCESS : ${dataItem.uri}")
        }.addOnFailureListener { exception ->
            Timber.tag("okhttp").d("SEND DATA TO PHONE FAIL : ${exception.message}")
        }
    }

    companion object {
        const val KEY_RECORD = "KEY_RECORD"
        const val KEY_START = "KEY_START"
        const val KEY_END = "KEY_END"

        const val PATH_RECORD = "/record"
        const val PATH_START = "/start"
        const val PATH_END = "/end"
    }
}
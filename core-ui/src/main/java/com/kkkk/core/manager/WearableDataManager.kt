package com.kkkk.core.manager

import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearableDataManager @Inject constructor(
    private val dataClient: DataClient
) {

    fun sendIntToWearable(key: String, value: Int, path: String): Task<DataItem> {
        Timber.tag("okhttp").d("START SENDING DATA TO WEARABLE")
        // DataMapRequest 생성
        val putDataMapReq = PutDataMapRequest.create(path).apply {
            dataMap.putInt(key, value)
        }

        // DataRequest로 변환
        val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()

        // DataClient를 통해 데이터 전송
        return dataClient.putDataItem(putDataReq).addOnSuccessListener { dataItem ->
            Timber.tag("okhttp").d("SEND DATA TO WEARABLE SUCCESS : ${dataItem.uri}")
        }.addOnFailureListener { exception ->
            Timber.tag("okhttp").d("SEND DATA TO WEARABLE FAIL : ${exception.message}")
        }
    }
}
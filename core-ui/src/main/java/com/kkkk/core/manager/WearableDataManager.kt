package com.kkkk.core.manager

import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearableDataManager @Inject constructor(
    private val dataClient: DataClient
) {

    fun sendIntToWearable(key: String, value: Int): Task<DataItem> {
        // DataMapRequest 생성
        val putDataMapReq = PutDataMapRequest.create("/int_data").apply {
            dataMap.putInt(key, value)
        }

        // DataRequest로 변환
        val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()

        // DataClient를 통해 데이터 전송
        return dataClient.putDataItem(putDataReq).addOnSuccessListener { dataItem ->
            // 데이터 전송 성공
            println("Data sent successfully, uri: ${dataItem.uri}")
        }.addOnFailureListener { exception ->
            // 데이터 전송 실패
            println("Failed to send data: ${exception.message}")
        }
    }
}
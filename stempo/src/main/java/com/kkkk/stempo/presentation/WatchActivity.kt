/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.kkkk.stempo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.kkkk.stempo.presentation.home.HomeScreen
import com.kkkk.stempo.presentation.manager.WearableDataManager
import com.kkkk.stempo.presentation.manager.WearableDataManager.Companion.KEY_RECORD
import com.kkkk.stempo.presentation.manager.WearableDataManager.Companion.PATH_RECORD
import com.kkkk.stempo.presentation.theme.StempoandroidTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WatchActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            StempoandroidTheme {
                HomeScreen()
            }
        }

        // TODO 이 함수로 정지 시 결과값 전송
        WearableDataManager(Wearable.getDataClient(this)).sendIntToPhone(
            PATH_RECORD,
            KEY_RECORD,
            50
        )
    }

    override fun onResume() {
        super.onResume()
        Timber.tag("okhttp").d("LISTENER : ADDED")
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Timber.tag("okhttp").d("LISTENER : ON DATA CHANGED")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo(PATH_BPM) == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            val bpm = getInt(KEY_BPM)
                            Timber.tag("okhttp").d("LISTENER : DATA RECEIVED : $bpm")
                            // TODO 여기서 bpm 받아서 초기값으로 설정
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

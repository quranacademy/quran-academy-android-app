package org.quranacademy.quran.radio.data.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.network.NetworkChecker
import org.quranacademy.quran.radio.data.RadioException
import org.quranacademy.quran.radio.data.di.RadioScope
import org.quranacademy.quran.radio.data.radio.RadioPlayer
import org.quranacademy.quran.radio.data.radio.RadioState
import javax.inject.Inject

class RadioManager @Inject constructor(
        @RadioScope private val coroutineScope: CoroutineScope,
        private val connectionHelper: RadioConnectionHelper,
        private val networkChecker: NetworkChecker,
        private val radioPlayer: RadioPlayer,
        private val radioData: RadioData
) {

    private lateinit var stationInfo: StationInfo

    init {
        coroutineScope.launch {
            radioPlayer.stateChangeUpdates()
                    .collect { state ->
                        radioData.playbackState = state
                    }
        }
    }

    fun playPause() {
        if (radioPlayer.isPlaying()) {
            radioPlayer.pause()
        } else {
            radioPlayer.play()
        }
    }

    fun play() = coroutineScope.launch(Dispatchers.IO) {
        if (!networkChecker.isConnected) {
            radioData.onPlaybackError(RadioException.Network())
        }

        if (!::stationInfo.isInitialized) {
            val url = try {
                connectionHelper.getRadioUrl("https://hqa.io/radio")
            } catch (error: Exception) {
                radioData.onPlaybackError(RadioException.Network())
                return@launch
            }
            val type = connectionHelper.detectContentType(url)
            this@RadioManager.stationInfo = StationInfo(
                    url = url,
                    streamContent = type.type
            )
        }

        val isStopped = radioPlayer.playbackState.let {
            it == RadioState.IDLE || it == RadioState.ERROR
        }
        if (isStopped) {
            radioPlayer.connect(stationInfo)
        }

        coroutineScope.launch(Dispatchers.Main) {
            radioPlayer.play()
        }
    }

    fun pause() = radioPlayer.pause()

    fun stop() = radioPlayer.stop()

    fun metadataChanged() = radioPlayer.metadataUpdates()

}
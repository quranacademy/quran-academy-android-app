package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.headset

import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.lifecycle.ServiceLifecycleObserver
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.data.quranplayer.HQAAudioPlayer
import org.quranacademy.quran.player.data.quranplayer.PauseReason
import timber.log.Timber
import javax.inject.Inject

class HeadsetPlaybackController @Inject constructor(
        private val context: Context,
        private val audioPlayer: HQAAudioPlayer
) : ServiceLifecycleObserver {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate() {

        coroutineScope.launch {
            HeadsetPlugReceiver.events(context)
                    .filter { it == HeadsetPlugReceiver.HeadsetState.PLUGGED }
                    .collect { headsetPlugged() }
        }

        coroutineScope.launch {
            FlowBroadcast.register(context, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
                    .collect { audioBecomingNoisy() }
        }
    }

    override fun onDestroy() {
        parentJob.cancel()
    }

    private fun headsetPlugged() {
        if (audioPlayer.pauseReason == PauseReason.BECAUSE_HEADSET) {
            audioPlayer.play()
        }
    }

    private fun audioBecomingNoisy() {
        Timber.d("audio becoming noisy. playState=${audioPlayer.playbackState}")
        if (audioPlayer.playbackState === PlayerState.PLAYING) {
            // pause audio when headphones are unplugged
            audioPlayer.pause(PauseReason.BECAUSE_HEADSET)
        }
    }

}
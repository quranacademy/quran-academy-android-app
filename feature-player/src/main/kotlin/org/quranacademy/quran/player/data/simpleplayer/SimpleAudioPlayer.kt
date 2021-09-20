package org.quranacademy.quran.player.data.simpleplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


class SimpleAudioPlayer @Inject constructor(
        private val context: Context
) {

    //player
    private val player: SimpleExoPlayer
    private val stateChangeUpdates = BroadcastChannel<SimpleAudioPlayerState>(1)
    var playbackState: SimpleAudioPlayerState = SimpleAudioPlayerState.IDLE
        private set(value) {
            field = value
            GlobalScope.launch { stateChangeUpdates.send(value) }
        }

    init {
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        val trackSelector = DefaultTrackSelector(context)

        player = SimpleExoPlayer.Builder(context)
                .setBandwidthMeter(bandwidthMeter)
                .setTrackSelector(trackSelector)
                .build()
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    this@SimpleAudioPlayer.playbackState = SimpleAudioPlayerState.LOADING
                }

                if (playbackState == Player.STATE_READY) {
                    this@SimpleAudioPlayer.playbackState = if (playWhenReady) {
                        SimpleAudioPlayerState.PLAYING
                    } else {
                        SimpleAudioPlayerState.PAUSED
                    }
                }

                if (playbackState == Player.STATE_IDLE) {
                    this@SimpleAudioPlayer.playbackState = SimpleAudioPlayerState.IDLE
                }

                //плеер завершил проигрывание
                if (playbackState == Player.STATE_ENDED) {
                    this@SimpleAudioPlayer.playbackState = SimpleAudioPlayerState.FINISHED
                }
            }
        })
    }

    fun playFromRaw(rawAudioResId: Int) {
        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(rawAudioResId))
        val rawDataSource = RawResourceDataSource(context)
        rawDataSource.open(dataSpec)
        startPlayback(rawDataSource.uri!!, null)
    }

    fun playFromFile(
            audioFile: File,
            onPlayingFinishedListener: ((isFinished: Boolean) -> Unit)?
    ) {
        val dataSpec = DataSpec(Uri.fromFile(audioFile))
        val fileDataSource = FileDataSource()
        fileDataSource.open(dataSpec)
        startPlayback(fileDataSource.uri!!, onPlayingFinishedListener)
    }

    fun play() {
        if (!isPlaying()) {
            player.play()
        }
    }

    fun pause() {
        if (isPlaying()) {
            player.pause()
        }
    }

    fun stop() {
        if (isStarted()) {
            player.stop()
            playbackState = SimpleAudioPlayerState.IDLE
        }
    }

    fun isStarted(): Boolean = playbackState != SimpleAudioPlayerState.IDLE && playbackState != SimpleAudioPlayerState.ERROR

    fun isPlaying(): Boolean = playbackState == SimpleAudioPlayerState.PLAYING

    fun stateChangeUpdates(): Flow<SimpleAudioPlayerState> = stateChangeUpdates.asFlow()

    private fun startPlayback(
            fileUri: Uri,
            onPlayingFinishedListener: ((isStopped: Boolean) -> Unit)?
    ) {
        stop()
        player.setMediaItem(MediaItem.fromUri(fileUri))
        player.prepare()

        val parentJob = Job()
        val listenerContext = Dispatchers.Main + parentJob
        GlobalScope.launch(context = listenerContext) {
            stateChangeUpdates.asFlow().collect { state ->
                if (state == SimpleAudioPlayerState.IDLE || state == SimpleAudioPlayerState.FINISHED) {
                    val isFinished = state == SimpleAudioPlayerState.FINISHED
                    onPlayingFinishedListener?.invoke(isFinished)
                    parentJob.cancel()
                }
            }
        }

        play()
    }

}
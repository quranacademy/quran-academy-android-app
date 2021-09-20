package org.quranacademy.quran.player.data.quranplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.di.playerservice.PlayerServiceScope
import javax.inject.Inject

class HQAAudioPlayer @Inject constructor(
        @PlayerServiceScope
        private val coroutineScope: CoroutineScope,
        context: Context
) {

    //player
    private val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.packageName))
    private val player: SimpleExoPlayer
    private val stateChangeUpdates = BroadcastChannel<PlayerState>(2)
    var playbackState: PlayerState = PlayerState.IDLE
        private set(value) {
            field = value
            coroutineScope.launch { stateChangeUpdates.send(value) }
        }
    var pauseReason: PauseReason = PauseReason.NONE
        private set

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
                    this@HQAAudioPlayer.playbackState = PlayerState.LOADING
                }

                if (playbackState == Player.STATE_READY) {
                    this@HQAAudioPlayer.playbackState = if (playWhenReady) PlayerState.PLAYING else PlayerState.PAUSED
                }

                //плеер завершил проигрывание
                if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                    this@HQAAudioPlayer.playbackState = PlayerState.IDLE
                }
            }
        })
    }

    fun playAudio(ayahAudio: AyahAudio) {
        seekTo(0)
        val audioSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(ayahAudio.audioFile))
        player.prepare(audioSource)
        play()
    }

    fun playPause() {
        if (isPlaying()) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (!isPlaying()) {
            player.playWhenReady = true
        }
    }

    fun pause(pauseReason: PauseReason = PauseReason.NONE) {
        if (isPlaying()) {
            this.pauseReason = pauseReason
            player.playWhenReady = false
        }
    }

    /**
     * Pauses, but does not abandon audio focus and does not schedule the stop timer.
     */
    fun pauseTemporary() {
        player.playWhenReady = false
        pauseReason = PauseReason.NONE
    }

    fun seekTo(position: Long) = player.seekTo(position)

    fun stop() {
        if (isStarted()) {
            player.stop()
            playbackState = PlayerState.IDLE
        }
    }

    fun isStarted(): Boolean = playbackState != PlayerState.IDLE && playbackState != PlayerState.ERROR

    fun isPlaying(): Boolean = playbackState == PlayerState.PLAYING

    fun getCurrentPlaybackPosition(): Long = player.currentPosition

    fun stateChangeUpdates(): Flow<PlayerState> = stateChangeUpdates.asFlow()


}
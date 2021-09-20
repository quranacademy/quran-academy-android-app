package org.quranacademy.quran.memorization.mvp.memorization.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.recitationsrepository.AudioPathProvider
import java.io.File
import javax.inject.Inject


class MemorizationPlayer @Inject constructor(
        context: Context,
        private val audioPathProvider: AudioPathProvider
) {

    //player
    private val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.packageName))
    private val player: SimpleExoPlayer
    private val stateChangeUpdates = BroadcastChannel<MemorizationPlayerState>(1)
    var playbackState: MemorizationPlayerState = MemorizationPlayerState.IDLE
        private set(value) {
            field = value
            GlobalScope.launch { stateChangeUpdates.send(value) }
        }

    init {
        // 1. Create a default TrackSelector
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        val trackSelector = DefaultTrackSelector(context)

        // 2. Create the player
        player = SimpleExoPlayer.Builder(context)
                .setBandwidthMeter(bandwidthMeter)
                .setTrackSelector(trackSelector)
                .build()
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    this@MemorizationPlayer.playbackState = MemorizationPlayerState.LOADING
                }

                if (playbackState == Player.STATE_READY) {
                    this@MemorizationPlayer.playbackState =
                            if (playWhenReady) MemorizationPlayerState.PLAYING
                            else MemorizationPlayerState.PAUSED
                }

                //плеер завершил проигрывание
                if (playbackState == Player.STATE_ENDED) {
                    this@MemorizationPlayer.playbackState = MemorizationPlayerState.IDLE
                }

            }
        })
    }

    suspend fun playAudio(ayahId: AyahId, recitation: Recitation, onPlaybackFinished: suspend () -> Unit) {
        stop()

        val audioFile = File(audioPathProvider.getAyahAudioPath(ayahId.surahNumber, ayahId.ayahNumber, recitation))
        val audioDataSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(audioFile))

        player.prepare(audioDataSource)
        coroutineScope {
            val parentJob = Job()
            launch(context = Dispatchers.Main + parentJob) {
                stateChangeUpdates.asFlow().collect { state ->
                    if (isActive && state == MemorizationPlayerState.IDLE) {
                        onPlaybackFinished()
                        cancel()
                    }
                }
            }
        }

        play()
    }

    fun play() {
        if (!isPlaying()) {
            player.playWhenReady = true
        }
    }

    fun pause() {
        if (isPlaying()) {
            player.playWhenReady = false
        }
    }

    fun stop() {
        if (isStarted()) {
            player.stop()
            playbackState = MemorizationPlayerState.IDLE
        }
    }

    fun isStarted(): Boolean = playbackState != MemorizationPlayerState.IDLE && playbackState != MemorizationPlayerState.ERROR

    fun isPlaying(): Boolean = playbackState == MemorizationPlayerState.PLAYING

    fun stateChangeUpdates(): Flow<MemorizationPlayerState> = stateChangeUpdates.asFlow()


}
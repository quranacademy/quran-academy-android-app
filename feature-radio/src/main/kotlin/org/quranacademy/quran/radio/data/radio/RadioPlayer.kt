package org.quranacademy.quran.radio.data.radio

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.metadata.icy.IcyInfo
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import org.quranacademy.quran.radio.data.RadioConstants
import org.quranacademy.quran.radio.data.di.RadioScope
import org.quranacademy.quran.radio.data.manager.StationInfo
import javax.inject.Inject


class RadioPlayer @Inject constructor(
        @RadioScope private val coroutineScope: CoroutineScope,
        private val context: Context
) {

    //player
    private val player: SimpleExoPlayer
    private val stateChangeUpdates = BroadcastChannel<RadioState>(2)
    private val metadataUpdates = BroadcastChannel<RadioMetadata>(2)
    var playbackState: RadioState = RadioState.IDLE
        private set(value) {
            field = value
            coroutineScope.launch { stateChangeUpdates.send(value) }
        }
    var pauseReason: RadioPauseReason = RadioPauseReason.NONE
        private set

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
                    this@RadioPlayer.playbackState = RadioState.PREPARING
                }

                if (playbackState == Player.STATE_READY) {
                    this@RadioPlayer.playbackState = if (playWhenReady) RadioState.PLAYING else RadioState.PAUSED
                }

                //плеер завершил проигрывание
                if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                    this@RadioPlayer.playbackState = RadioState.IDLE
                }
            }
        })
        player.addMetadataOutput { metadata ->
            coroutineScope.launch {
                for (i in 0 until metadata.length()) {
                    val entry = metadata[i]
                    if (entry is IcyInfo) {
                        val title = entry.title ?: ""
                        metadataUpdates.send(RadioMetadata(title))
                    }
                }
            }
        }
    }

    fun connect(stationInfo: StationInfo) {
        preparePlayer(stationInfo)
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

    fun pause(pauseReason: RadioPauseReason = RadioPauseReason.NONE) {
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
        pauseReason = RadioPauseReason.NONE
    }

    fun seekTo(position: Long) = player.seekTo(position)

    fun stop() {
        if (isStarted()) {
            player.stop()
            playbackState = RadioState.IDLE
        }
    }

    fun isStarted(): Boolean = playbackState != RadioState.IDLE && playbackState != RadioState.ERROR

    fun isPlaying(): Boolean = playbackState == RadioState.PLAYING

    fun getCurrentPlaybackPosition(): Long = player.currentPosition

    fun stateChangeUpdates(): Flow<RadioState> = stateChangeUpdates.asFlow()

    fun metadataUpdates(): Flow<RadioMetadata> = metadataUpdates.asFlow()

    private fun preparePlayer(stationInfo: StationInfo) {
        // create DataSource.Factory - produces DataSource instances through which media data is loaded
        val dataSourceFactory = createDataSourceFactory(context, null)

        val isHls = stationInfo.streamContent in RadioConstants.MIME_TYPE_HLS
                || stationInfo.streamContent in RadioConstants.MIME_TYPES_M3U
        // create MediaSource
        val mediaSource = if (isHls) {
            // HLS media source
            //Toast.makeText(this, this.getString(R.string.toastmessage_stream_may_not_work), Toast.LENGTH_LONG).show()
            HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(stationInfo.url))
        } else {
            // MPEG or OGG media source
            ProgressiveMediaSource.Factory(dataSourceFactory)
                    .setContinueLoadingCheckIntervalBytes(32)
                    .createMediaSource(Uri.parse(stationInfo.url))
        }

        // prepare player with source
        player.prepare(mediaSource)
    }

    /* Creates a DataSourceFactor that supports http redirects */
    private fun createDataSourceFactory(
            context: Context,
            listener: TransferListener?
    ): DefaultDataSourceFactory {
        val userAgent = Util.getUserAgent(context, context.packageName)
        // Credit: https://stackoverflow.com/questions/41517440/exoplayer2-how-can-i-make-a-http-301-redirect-work
        // Default parameters, except allowCrossProtocolRedirects is true
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                userAgent,
                listener,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
        )
        return DefaultDataSourceFactory(
                context,
                listener,
                httpDataSourceFactory
        )
    }

}
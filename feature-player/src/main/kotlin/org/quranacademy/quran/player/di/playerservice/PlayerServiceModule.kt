package org.quranacademy.quran.player.di.playerservice

import android.support.v4.media.session.MediaSessionCompat
import kotlinx.coroutines.CoroutineScope
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.toType
import org.quranacademy.quran.player.data.quranplayer.BasmalahPlayer
import org.quranacademy.quran.player.data.quranplayer.HQAAudioPlayer
import org.quranacademy.quran.player.data.quranplayer.downloading.PlayerAudioDownloadManager
import org.quranacademy.quran.player.data.quranplayer.notification.MediaSessionCallback
import org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.AudioFocusPlaybackController
import org.quranacademy.quran.player.data.quranplayer.playbackmanager.*
import org.quranacademy.quran.player.data.quranplayer.queue.QueueManager
import toothpick.config.Module

class PlayerServiceModule(coroutineScope: CoroutineScope) : Module() {

    init {
        bind(CoroutineScope::class.java)
                .withName(PlayerServiceScope::class.java)
                .toInstance(coroutineScope)

        bindSingleton<QuranAudioPlaybackManager>()
        bindSingleton<AudioFocusPlaybackController>()
        bindSingleton<HQAAudioPlayer>()
        bindSingleton<BasmalahPlayer>()
        bindSingleton<QueueManager>()
        bindSingleton<PlayerStateChangeHandler>()
        bindSingleton<AudioPlayerProgressUpdater>()
        bindSingleton<AyahPlaybackFinishedHandler>()

        bind(PlayerAudioDownloadManager.Listener::class)
                .toType(PlayerDownloadHandler::class)

        bind(MediaSessionCompat.Callback::class.java)
                .to(MediaSessionCallback::class.java)
    }

}
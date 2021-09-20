package org.quranacademy.quran.di.modules

import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.bindSingleton
import org.quranacademy.quran.di.toType
import org.quranacademy.quran.player.data.quranplayer.BasmalahPlayer
import org.quranacademy.quran.player.data.quranplayer.HQAAudioPlayer
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.player.data.quranplayer.playbackmanager.AyahPlaybackFinishedHandler
import org.quranacademy.quran.player.data.quranplayer.queue.QueueManager
import org.quranacademy.quran.player.data.quranplayer.service.PlayerServiceControl
import org.quranacademy.quran.player.domain.PlayerControl
import org.quranacademy.quran.radio.data.manager.RadioData
import org.quranacademy.quran.radio.data.service.RadioControlImpl
import org.quranacademy.quran.radio.domain.RadioControl
import toothpick.config.Module

class PlayerAndRadioModule : Module() {

    init {
        bindSingleton<PlaybackData>()
        bindSingleton<AyahPlaybackFinishedHandler>()
        bindSingleton<BasmalahPlayer>()
        bindSingleton<HQAAudioPlayer>()
        bindSingleton<QueueManager>()

        bind(RadioControl::class)
                .toType(RadioControlImpl::class)
                .singletonInScope()
        bindSingleton<RadioData>()

        bind(PlayerControl::class)
                .toType(PlayerServiceControl::class)
                .singletonInScope()
    }

}
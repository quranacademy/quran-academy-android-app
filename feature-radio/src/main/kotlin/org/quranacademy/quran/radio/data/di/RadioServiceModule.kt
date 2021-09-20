package org.quranacademy.quran.radio.data.di

import android.support.v4.media.session.MediaSessionCompat
import kotlinx.coroutines.CoroutineScope
import org.quranacademy.quran.radio.data.notification.RadioMediaSessionControl
import toothpick.config.Module

class RadioServiceModule(coroutineScope: CoroutineScope) : Module() {

    init {
        bind(CoroutineScope::class.java)
                .withName(RadioScope::class.java)
                .toInstance(coroutineScope)

        bind(MediaSessionCompat.Callback::class.java)
                .to(RadioMediaSessionControl::class.java)
    }

}
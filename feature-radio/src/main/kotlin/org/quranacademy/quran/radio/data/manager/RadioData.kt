package org.quranacademy.quran.radio.data.manager

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import org.quranacademy.quran.radio.data.RadioException
import org.quranacademy.quran.radio.data.radio.RadioState
import timber.log.Timber
import javax.inject.Inject

class RadioData @Inject constructor() {

    private val radioStateUpdates = BroadcastChannel<RadioState>(Channel.CONFLATED)
    private val radioErrorUpdates = BroadcastChannel<RadioException>(1)

    var playbackState: RadioState = RadioState.IDLE
        set(value) {
            if (value != field) {
                field = value
                GlobalScope.launch { radioStateUpdates.send(value) }
            }
        }

    fun onPlaybackError(error: RadioException) {
        GlobalScope.launch { radioErrorUpdates.send(error) }
        playbackState = RadioState.ERROR
        Timber.e(error)
    }

    fun radioStateUpdates(): Flow<RadioState> = radioStateUpdates.asFlow()

    fun radioErrorUpdates(): Flow<RadioException> = radioErrorUpdates.asFlow()

}
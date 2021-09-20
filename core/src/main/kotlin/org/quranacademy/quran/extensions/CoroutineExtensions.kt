package org.quranacademy.quran.extensions

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
fun <T> Flow<T>.mergeWith(other: Flow<T>): Flow<T> = channelFlow {
    coroutineScope {
        launch {
            other.collect {
                send(it)
            }
        }

        launch {
            collect {
                send(it)
            }
        }
    }
}
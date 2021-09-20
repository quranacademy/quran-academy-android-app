package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.headset

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

/**
 * Wraps a broadcast receiver in an observable that registers and unregisters based on the subscription.
 */
object FlowBroadcast {

    fun register(context: Context, filter: IntentFilter): Flow<Intent> = channelFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                launch { this@channelFlow.send(intent) }
            }
        }
        context.registerReceiver(receiver, filter)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

}
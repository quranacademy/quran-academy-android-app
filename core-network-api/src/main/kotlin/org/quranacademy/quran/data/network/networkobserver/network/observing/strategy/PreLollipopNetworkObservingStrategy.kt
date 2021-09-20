package org.quranacademy.quran.data.network.networkobserver.network.observing.strategy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.network.networkobserver.Connectivity
import org.quranacademy.quran.data.network.networkobserver.ReactiveNetwork.LOG_TAG
import org.quranacademy.quran.data.network.networkobserver.network.observing.NetworkObservingStrategy

/**
 * Network observing strategy for Android devices before Lollipop (API 20 or lower).
 * Uses Broadcast Receiver.
 */
class PreLollipopNetworkObservingStrategy : NetworkObservingStrategy {
    lateinit var receiver: BroadcastReceiver
    override fun observeNetworkConnectivity(context: Context): Flow<Connectivity> {
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)


        return flow {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    GlobalScope.launch {
                        emit(Connectivity.create(context))
                    }
                }

            }

            context.registerReceiver(receiver, filter)
        }
                .onStart { emit(Connectivity.create()) }
                .onCompletion {
                    withContext(Dispatchers.Main) {
                        tryToUnregisterReceiver(context, receiver)
                    }
                }

    }

    protected fun tryToUnregisterReceiver(context: Context, receiver: BroadcastReceiver) {
        try {
            context.unregisterReceiver(receiver)
        } catch (exception: Exception) {
            onError("receiver was already unregistered", exception)
        }

    }

    override fun onError(message: String, exception: Exception) {
        Log.e(LOG_TAG, message, exception)
    }

}

/*
 * Copyright (C) 2016 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quranacademy.quran.data.network.networkobserver.network.observing.strategy

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.network.networkobserver.Connectivity
import org.quranacademy.quran.data.network.networkobserver.ReactiveNetwork.LOG_TAG
import org.quranacademy.quran.data.network.networkobserver.network.observing.NetworkObservingStrategy

/**
 * Network observing strategy for devices with Android Marshmallow (API 23) or higher.
 * Uses Network Callback API and handles Doze mode.
 */
@TargetApi(23)
class MarshmallowNetworkObservingStrategy : NetworkObservingStrategy {

    private// it has to be initialized in the Observable due to Context
    var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityChannel = BroadcastChannel<Connectivity>(2)
    private val idleReceiver: BroadcastReceiver

    init {
        this.idleReceiver = createIdleBroadcastReceiver()
    }// networkCallback cannot be initialized here

    @SuppressLint("MissingPermission")
    override fun observeNetworkConnectivity(context: Context): Flow<Connectivity> {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager
        networkCallback = createNetworkCallback(context)

        registerIdleReceiver(context)

        val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .build()

        manager.registerNetworkCallback(request, networkCallback)

        return connectivityChannel.asFlow()
                .onStart { emit(Connectivity.create(context)) }
                .distinctUntilChanged()
                .apply {
                    this.onCompletion {
                        tryToUnregisterCallback(manager)
                        tryToUnregisterReceiver(context)
                    }
                }
    }

    protected fun registerIdleReceiver(context: Context) {
        val filter = IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
        context.registerReceiver(idleReceiver, filter)
    }

    protected fun createIdleBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                GlobalScope.launch {
                    if (isIdleMode(context)) {
                        onNext(Connectivity.create())
                    } else {
                        onNext(Connectivity.create(context))
                    }
                }
            }
        }
    }

    protected fun isIdleMode(context: Context): Boolean {
        val packageName = context.packageName
        val manager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnoringOptimizations = manager.isIgnoringBatteryOptimizations(packageName)
        return manager.isDeviceIdleMode && !isIgnoringOptimizations
    }

    protected fun tryToUnregisterCallback(manager: ConnectivityManager) {
        try {
            manager.unregisterNetworkCallback(networkCallback)
        } catch (exception: Exception) {
            onError(ERROR_MSG_NETWORK_CALLBACK, exception)
        }

    }

    protected fun tryToUnregisterReceiver(context: Context) {
        try {
            context.unregisterReceiver(idleReceiver)
        } catch (exception: Exception) {
            onError(ERROR_MSG_RECEIVER, exception)
        }

    }

    override fun onError(message: String, exception: Exception) {
        Log.e(LOG_TAG, message, exception)
    }

    protected fun createNetworkCallback(context: Context): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNext(Connectivity.create(context))
            }

            override fun onLost(network: Network) {
                onNext(Connectivity.create(context))
            }
        }
    }

    protected fun onNext(connectivity: Connectivity) {
        GlobalScope.launch { connectivityChannel.send(connectivity) }
    }

    companion object {
        protected val ERROR_MSG_NETWORK_CALLBACK = "could not unregister network callback"
        protected val ERROR_MSG_RECEIVER = "could not unregister receiver"
    }

}

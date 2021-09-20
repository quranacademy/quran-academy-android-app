package org.quranacademy.quran.data.network.networkobserver.network.observing.strategy

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.network.networkobserver.Connectivity
import org.quranacademy.quran.data.network.networkobserver.ReactiveNetwork.LOG_TAG
import org.quranacademy.quran.data.network.networkobserver.network.observing.NetworkObservingStrategy

/**
 * Network observing strategy for devices with Android Lollipop (API 21) or higher.
 * Uses Network Callback API.
 */
@TargetApi(21)
class LollipopNetworkObservingStrategy : NetworkObservingStrategy {

    private var networkCallback: NetworkCallback? = null

    @SuppressLint("MissingPermission")
    override fun observeNetworkConnectivity(context: Context): Flow<Connectivity> {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager

        return flow {
            coroutineScope {
                networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        launch { emit(Connectivity.create(context)) }
                    }

                    override fun onLost(network: Network) {
                        launch { emit(Connectivity.create(context)) }
                    }
                }
                val networkRequest = NetworkRequest.Builder().build()
                manager.registerNetworkCallback(networkRequest, networkCallback)
            }
        }
                .onCompletion { tryToUnregisterCallback(manager) }
                .onStart { emit(Connectivity.create(context)) }
                .distinctUntilChanged()
    }

    private fun tryToUnregisterCallback(manager: ConnectivityManager) {
        try {
            manager.unregisterNetworkCallback(networkCallback)
        } catch (exception: Exception) {
            onError("could not unregister network callback", exception)
        }

    }

    override fun onError(message: String, exception: Exception) {
        Log.e(LOG_TAG, message, exception)
    }

}

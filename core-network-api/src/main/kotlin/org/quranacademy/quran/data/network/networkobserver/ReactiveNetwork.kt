package org.quranacademy.quran.data.network.networkobserver

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.data.network.networkobserver.internet.observing.InternetObservingSettings
import org.quranacademy.quran.data.network.networkobserver.internet.observing.InternetObservingStrategy
import org.quranacademy.quran.data.network.networkobserver.internet.observing.error.ErrorHandler
import org.quranacademy.quran.data.network.networkobserver.network.observing.NetworkObservingStrategy
import org.quranacademy.quran.data.network.networkobserver.network.observing.strategy.LollipopNetworkObservingStrategy
import org.quranacademy.quran.data.network.networkobserver.network.observing.strategy.MarshmallowNetworkObservingStrategy
import org.quranacademy.quran.data.network.networkobserver.network.observing.strategy.PreLollipopNetworkObservingStrategy

/**
 * ReactiveNetwork is an Android library
 * listening network connection state and change of the WiFi signal strength
 * with RxJava Observables. It can be easily used with RxAndroid.
 */
object ReactiveNetwork {

    val LOG_TAG = "ReactiveNetwork"

    /**
     * Observes network connectivity. Information about network state, type and typeName are contained
     * in
     * observed Connectivity object.
     *
     * @param context Context of the activity or an application
     * @return RxJava Observable with Connectivity class containing information about network state,
     * type and typeName
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun observeNetworkConnectivity(context: Context): Flow<Connectivity> {
        val strategy: NetworkObservingStrategy

        if (Preconditions.isAtLeastAndroidMarshmallow()) {
            strategy = MarshmallowNetworkObservingStrategy()
        } else if (Preconditions.isAtLeastAndroidLollipop()) {
            strategy = LollipopNetworkObservingStrategy()
        } else {
            strategy = PreLollipopNetworkObservingStrategy()
        }

        return observeNetworkConnectivity(context, strategy)
    }

    /**
     * Observes network connectivity. Information about network state, type and typeName are contained
     * in observed Connectivity object. Moreover, allows you to define NetworkObservingStrategy.
     *
     * @param context Context of the activity or an application
     * @param strategy NetworkObserving strategy to be applied - you can use one of the existing
     * strategies [PreLollipopNetworkObservingStrategy],
     * [LollipopNetworkObservingStrategy] or create your own custom strategy
     * @return RxJava Observable with Connectivity class containing information about network state,
     * type and typeName
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun observeNetworkConnectivity(context: Context,
                                   strategy: NetworkObservingStrategy): Flow<Connectivity> {
        Preconditions.checkNotNull(context, "context == null")
        Preconditions.checkNotNull(strategy, "strategy == null")
        return strategy.observeNetworkConnectivity(context)
    }

    /**
     * Observes connectivity with the Internet with default settings. It pings remote host
     * (www.google.com) at port 80 every 2 seconds with 2 seconds of timeout. This operation is used
     * for determining if device is connected to the Internet or not. Please note that this method is
     * less efficient than [.observeNetworkConnectivity] method and consumes data
     * transfer, but it gives you actual information if device is connected to the Internet or not.
     *
     * @return RxJava Observable with Boolean - true, when we have an access to the Internet
     * and false if not
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    fun observeInternetConnectivity(): Flow<Boolean> {
        val settings = InternetObservingSettings.create()
        return observeInternetConnectivity(settings.strategy(), settings.initialInterval(),
                settings.interval(), settings.host(), settings.port(),
                settings.timeout(), settings.httpResponse(), settings.errorHandler())
    }

    /**
     * Observes connectivity with the Internet in a given time interval.
     *
     * @param settings Internet Observing Settings created via Builder pattern
     * @return RxJava Observable with Boolean - true, when we have connection with host and false if
     * not
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    fun observeInternetConnectivity(
            settings: InternetObservingSettings): Flow<Boolean> {
        return observeInternetConnectivity(
                settings.strategy(),
                settings.initialInterval(),
                settings.interval(),
                settings.host(),
                settings.port(),
                settings.timeout(),
                settings.httpResponse(),
                settings.errorHandler()
        )
    }

    /**
     * Observes connectivity with the Internet in a given time interval.
     *
     * @param strategy for observing Internet connectivity
     * @param initialIntervalInMs in milliseconds determining the delay of the first connectivity
     * check
     * @param intervalInMs in milliseconds determining how often we want to check connectivity
     * @param host for checking Internet connectivity
     * @param port for checking Internet connectivity
     * @param timeoutInMs for pinging remote host in milliseconds
     * @param httpResponse expected HTTP response code indicating that connection is established
     * @param errorHandler for handling errors during connectivity check
     * @return RxJava Observable with Boolean - true, when we have connection with host and false if
     * not
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    fun observeInternetConnectivity(
            strategy: InternetObservingStrategy,
            initialIntervalInMs: Int,
            intervalInMs: Int,
            host: String,
            port: Int,
            timeoutInMs: Int,
            httpResponse: Int,
            errorHandler: ErrorHandler
    ): Flow<Boolean> {
        checkStrategyIsNotNull(strategy)
        return strategy.observeInternetConnectivity(initialIntervalInMs, intervalInMs, host, port,
                timeoutInMs, httpResponse, errorHandler)
    }

    /**
     * Checks connectivity with the Internet. This operation is performed only once.
     *
     * @return RxJava Single with Boolean - true, when we have an access to the Internet
     * and false if not
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    suspend fun checkInternetConnectivity(): Boolean {
        val settings = InternetObservingSettings.create()
        return checkInternetConnectivity(settings.strategy(), settings.host(), settings.port(),
                settings.timeout(), settings.httpResponse(), settings.errorHandler())
    }

    /**
     * Checks connectivity with the Internet. This operation is performed only once.
     *
     * @param settings Internet Observing Settings created via Builder pattern
     * @return RxJava Single with Boolean - true, when we have connection with host and false if
     * not
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    suspend fun checkInternetConnectivity(settings: InternetObservingSettings): Boolean {
        return checkInternetConnectivity(settings.strategy(), settings.host(), settings.port(),
                settings.timeout(), settings.httpResponse(), settings.errorHandler())
    }

    /**
     * Checks connectivity with the Internet. This operation is performed only once.
     *
     * @param strategy for observing Internet connectivity
     * @param host for checking Internet connectivity
     * @param port for checking Internet connectivity
     * @param timeoutInMs for pinging remote host in milliseconds
     * @param httpResponse expected HTTP response code indicating that connection is established
     * @param errorHandler for handling errors during connectivity check
     * @return RxJava Single with Boolean - true, when we have connection with host and false if
     * not
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    suspend fun checkInternetConnectivity(
            strategy: InternetObservingStrategy,
            host: String, port: Int, timeoutInMs: Int, httpResponse: Int,
            errorHandler: ErrorHandler
    ): Boolean {
        checkStrategyIsNotNull(strategy)
        return strategy.checkInternetConnectivity(host, port, timeoutInMs, httpResponse, errorHandler)
    }

    private fun checkStrategyIsNotNull(strategy: InternetObservingStrategy) {
        Preconditions.checkNotNull(strategy, "strategy == null")
    }

}

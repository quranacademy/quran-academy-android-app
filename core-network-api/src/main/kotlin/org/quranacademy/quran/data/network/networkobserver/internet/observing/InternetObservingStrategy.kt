package org.quranacademy.quran.data.network.networkobserver.internet.observing

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.data.network.networkobserver.internet.observing.error.ErrorHandler

/**
 * Internet observing strategy allows to implement different strategies for monitoring connectivity
 * with the Internet.
 */
interface InternetObservingStrategy {

    /**
     * Gets default remote ping host for a given Internet Observing Strategy
     *
     * @return String with a ping host used in the current strategy
     */
    val defaultPingHost: String

    /**
     * Observes connectivity with the Internet by opening socket connection with remote host in a
     * given interval infinitely
     *
     * @param initialIntervalInMs in milliseconds determining the delay of the first connectivity
     * check
     * @param intervalInMs        in milliseconds determining how often we want to check connectivity
     * @param host                for checking Internet connectivity
     * @param port                for checking Internet connectivity
     * @param timeoutInMs         for pinging remote host in milliseconds
     * @param errorHandler        for handling errors while checking connectivity
     * @return RxJava Observable with Boolean - true, when we have connection with host and false if
     * not
     */
    fun observeInternetConnectivity(
            initialIntervalInMs: Int,
            intervalInMs: Int, host: String, port: Int, timeoutInMs: Int,
            httpResponse: Int, errorHandler: ErrorHandler
    ): Flow<Boolean>

    /**
     * Observes connectivity with the Internet by opening socket connection with remote host once
     *
     * @param host         for checking Internet connectivity
     * @param port         for checking Internet connectivity
     * @param timeoutInMs  for pinging remote host in milliseconds
     * @param errorHandler for handling errors while checking connectivity
     * @return RxJava Single with Boolean - true, when we have connection with host and false if
     * not
     */
    suspend fun checkInternetConnectivity(
            host: String, port: Int,
            timeoutInMs: Int,
            httpResponse: Int,
            errorHandler: ErrorHandler
    ): Boolean
}

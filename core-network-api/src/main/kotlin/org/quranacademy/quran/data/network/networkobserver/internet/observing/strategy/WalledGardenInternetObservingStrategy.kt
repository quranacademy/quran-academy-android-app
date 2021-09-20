package org.quranacademy.quran.data.network.networkobserver.internet.observing.strategy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.network.networkobserver.Preconditions
import org.quranacademy.quran.data.network.networkobserver.internet.observing.InternetObservingStrategy
import org.quranacademy.quran.data.network.networkobserver.internet.observing.error.ErrorHandler
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * Walled Garden Strategy for monitoring connectivity with the Internet.
 * This strategy handle use case of the countries behind Great Firewall (e.g. China),
 * which does not has access to several websites like Google. It such case, different HTTP responses
 * are generated. Instead HTTP 200 (OK), we got HTTP 204 (NO CONTENT), but it still can tell us
 * if a device is connected to the Internet or not.
 */
class WalledGardenInternetObservingStrategy : InternetObservingStrategy {

    override val defaultPingHost: String
        get() = DEFAULT_HOST

    lateinit var timer: Timer

    override fun observeInternetConnectivity(
            initialIntervalInMs: Int,
            intervalInMs: Int,
            host: String,
            port: Int,
            timeoutInMs: Int,
            httpResponse: Int,
            errorHandler: ErrorHandler
    ): Flow<Boolean> {

        Preconditions.checkGreaterOrEqualToZero(initialIntervalInMs,
                "initialIntervalInMs is not a positive number")
        Preconditions.checkGreaterThanZero(intervalInMs, "intervalInMs is not a positive number")
        checkGeneralPreconditions(host, port, timeoutInMs, httpResponse, errorHandler)

        val adjustedHost = adjustHost(host)

        return flow {
            timer = fixedRateTimer(
                    name = "NetworkConnectivity",
                    daemon = false,
                    initialDelay = initialIntervalInMs.toLong(),
                    period = intervalInMs.toLong()
            ) {
                GlobalScope.launch { emit(isConnected(adjustedHost, port, timeoutInMs, httpResponse, errorHandler)) }
            }
        }
                .flowOn(Dispatchers.IO)
                .distinctUntilChanged()
                .onCompletion { timer.cancel() }
    }

    override suspend fun checkInternetConnectivity(
            host: String,
            port: Int,
            timeoutInMs: Int,
            httpResponse: Int,
            errorHandler: ErrorHandler
    ): Boolean {
        checkGeneralPreconditions(host, port, timeoutInMs, httpResponse, errorHandler)
        return isConnected(host, port, timeoutInMs, httpResponse, errorHandler)
    }

    protected fun adjustHost(host: String): String {
        return if (!host.startsWith(HTTP_PROTOCOL) && !host.startsWith(HTTPS_PROTOCOL)) {
            HTTP_PROTOCOL + host
        } else host

    }

    private fun checkGeneralPreconditions(host: String, port: Int, timeoutInMs: Int,
                                          httpResponse: Int, errorHandler: ErrorHandler) {
        Preconditions.checkNotNullOrEmpty(host, "host is null or empty")
        Preconditions.checkGreaterThanZero(port, "port is not a positive number")
        Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number")
        Preconditions.checkNotNull(errorHandler, "errorHandler is null")
        Preconditions.checkNotNull(httpResponse, "httpResponse is null")
        Preconditions.checkGreaterThanZero(httpResponse, "httpResponse is not a positive number")
    }

    protected fun isConnected(
            host: String,
            port: Int,
            timeoutInMs: Int,
            httpResponse: Int,
            errorHandler: ErrorHandler
    ): Boolean {
        var urlConnection: HttpURLConnection? = null
        return try {
            urlConnection = createHttpUrlConnection(host, port, timeoutInMs)
            urlConnection!!.responseCode == httpResponse
        } catch (e: IOException) {
            errorHandler.handleError(e, "Could not establish connection with WalledGardenStrategy")
            java.lang.Boolean.FALSE
        } finally {
            urlConnection?.disconnect()
        }
    }

    @Throws(IOException::class)
    protected fun createHttpUrlConnection(host: String, port: Int,
                                          timeoutInMs: Int): HttpURLConnection {
        val initialUrl = URL(host)
        val url = URL(initialUrl.protocol, initialUrl.host, port, initialUrl.file)
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.connectTimeout = timeoutInMs
        urlConnection.readTimeout = timeoutInMs
        urlConnection.instanceFollowRedirects = false
        urlConnection.useCaches = false
        return urlConnection
    }

    companion object {
        private val DEFAULT_HOST = "http://clients3.google.com/generate_204"
        private val HTTP_PROTOCOL = "http://"
        private val HTTPS_PROTOCOL = "https://"
    }
}

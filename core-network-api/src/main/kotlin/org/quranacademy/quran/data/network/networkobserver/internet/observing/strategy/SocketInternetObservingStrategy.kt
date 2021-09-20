package org.quranacademy.quran.data.network.networkobserver.internet.observing.strategy

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.data.network.networkobserver.Preconditions
import org.quranacademy.quran.data.network.networkobserver.internet.observing.InternetObservingStrategy
import org.quranacademy.quran.data.network.networkobserver.internet.observing.error.ErrorHandler
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Socket strategy for monitoring connectivity with the Internet.
 * It monitors Internet connectivity via opening socket connection with the remote host.
 */
class SocketInternetObservingStrategy : InternetObservingStrategy {

    override val defaultPingHost: String
        get() = DEFAULT_HOST

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
        checkGeneralPreconditions(host, port, timeoutInMs, errorHandler)

        val adjustedHost = adjustHost(host)

        //Observable.interval(initialIntervalInMs, intervalInMs, TimeUnit.MILLISECONDS, Schedulers.io())
        //       .map(object : Function<Long, Boolean>() {
        //           @Throws(Exception::class)
        //           fun apply(@NonNull tick: Long?): Boolean? {
        //               return isConnected(adjustedHost, port, timeoutInMs, errorHandler)
        //           }
        //       }).distinctUntilChanged()
        TODO()
    }

    override suspend fun checkInternetConnectivity(
            host: String,
            port: Int,
            timeoutInMs: Int,
            httpResponse: Int,
            errorHandler: ErrorHandler
    ): Boolean {
        checkGeneralPreconditions(host, port, timeoutInMs, errorHandler)
        return isConnected(host, port, timeoutInMs, errorHandler)
    }

    /**
     * adjusts host to needs of SocketInternetObservingStrategy
     *
     * @return transformed host
     */
    protected fun adjustHost(host: String): String {
        if (host.startsWith(HTTP_PROTOCOL)) {
            return host.replace(HTTP_PROTOCOL, EMPTY_STRING)
        } else if (host.startsWith(HTTPS_PROTOCOL)) {
            return host.replace(HTTPS_PROTOCOL, EMPTY_STRING)
        }
        return host
    }

    private fun checkGeneralPreconditions(host: String, port: Int, timeoutInMs: Int,
                                          errorHandler: ErrorHandler) {
        Preconditions.checkNotNullOrEmpty(host, "host is null or empty")
        Preconditions.checkGreaterThanZero(port, "port is not a positive number")
        Preconditions.checkGreaterThanZero(timeoutInMs, "timeoutInMs is not a positive number")
        Preconditions.checkNotNull(errorHandler, "errorHandler is null")
    }

    /**
     * checks if device is connected to given host at given port
     *
     * @param host         to connect
     * @param port         to connect
     * @param timeoutInMs  connection timeout
     * @param errorHandler error handler for socket connection
     * @return boolean true if connected and false if not
     */
    protected fun isConnected(host: String, port: Int, timeoutInMs: Int,
                              errorHandler: ErrorHandler): Boolean {
        val socket = Socket()
        return isConnected(socket, host, port, timeoutInMs, errorHandler)
    }

    /**
     * checks if device is connected to given host at given port
     *
     * @param socket       to connect
     * @param host         to connect
     * @param port         to connect
     * @param timeoutInMs  connection timeout
     * @param errorHandler error handler for socket connection
     * @return boolean true if connected and false if not
     */
    protected fun isConnected(socket: Socket, host: String, port: Int,
                              timeoutInMs: Int, errorHandler: ErrorHandler): Boolean {
        var isConnected: Boolean
        try {
            socket.connect(InetSocketAddress(host, port), timeoutInMs)
            isConnected = socket.isConnected
        } catch (e: IOException) {
            isConnected = java.lang.Boolean.FALSE
        } finally {
            try {
                socket.close()
            } catch (exception: IOException) {
                errorHandler.handleError(exception, "Could not close the socket")
            }

        }
        return isConnected
    }

    companion object {

        private val EMPTY_STRING = ""
        private val DEFAULT_HOST = "www.google.com"
        private val HTTP_PROTOCOL = "http://"
        private val HTTPS_PROTOCOL = "https://"
    }
}

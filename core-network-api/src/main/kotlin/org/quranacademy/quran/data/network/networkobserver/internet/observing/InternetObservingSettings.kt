package org.quranacademy.quran.data.network.networkobserver.internet.observing

import org.quranacademy.quran.data.network.networkobserver.internet.observing.error.DefaultErrorHandler
import org.quranacademy.quran.data.network.networkobserver.internet.observing.error.ErrorHandler
import org.quranacademy.quran.data.network.networkobserver.internet.observing.strategy.WalledGardenInternetObservingStrategy

import java.net.HttpURLConnection

/**
 * Contains state of internet connectivity settings.
 * We should use its Builder for creating new settings
 */
// I want to have the same method names as variable names on purpose
class InternetObservingSettings constructor(
        private val initialInterval: Int,
        private val interval: Int,
        private val host: String,
        private val port: Int,
        private val timeout: Int,
        private val httpResponse: Int,
        private val errorHandler: ErrorHandler,
        private val strategy: InternetObservingStrategy) {

    private constructor(
            builder: Builder = builder()
    ) : this(
            builder.initialInterval,
            builder.interval,
            builder.host,
            builder.port,
            builder.timeout,
            builder.httpResponse,
            builder.errorHandler,
            builder.strategy) {
    }

    /**
     * @return initial ping interval in milliseconds
     */
    fun initialInterval(): Int {
        return initialInterval
    }

    /**
     * @return ping interval in milliseconds
     */
    fun interval(): Int {
        return interval
    }

    /**
     * @return ping host
     */
    fun host(): String {
        return host
    }

    /**
     * @return ping port
     */
    fun port(): Int {
        return port
    }

    /**
     * @return ping timeout in milliseconds
     */
    fun timeout(): Int {
        return timeout
    }

    fun httpResponse(): Int {
        return httpResponse
    }

    /**
     * @return error handler for pings and connections
     */
    fun errorHandler(): ErrorHandler {
        return errorHandler
    }

    /**
     * @return internet observing strategy
     */
    fun strategy(): InternetObservingStrategy {
        return strategy
    }

    /**
     * Settings builder, which contains default parameters
     */
    class Builder {

        var initialInterval = 0
        var interval = 2000
        var host = "http://clients3.google.com/generate_204"
        var port = 80
        var timeout = 2000
        var httpResponse = HttpURLConnection.HTTP_NO_CONTENT
        var errorHandler: ErrorHandler = DefaultErrorHandler()
        var strategy: InternetObservingStrategy = WalledGardenInternetObservingStrategy()

        /**
         * sets initial ping interval in milliseconds
         *
         * @param initialInterval in milliseconds
         * @return Builder
         */
        fun initialInterval(initialInterval: Int): Builder {
            this.initialInterval = initialInterval
            return this
        }

        /**
         * sets ping interval in milliseconds
         *
         * @param interval in milliseconds
         * @return Builder
         */
        fun interval(interval: Int): Builder {
            this.interval = interval
            return this
        }

        /**
         * sets ping host
         *
         * @return Builder
         */
        fun host(host: String): Builder {
            this.host = host
            return this
        }

        /**
         * sets ping port
         *
         * @return Builder
         */
        fun port(port: Int): Builder {
            this.port = port
            return this
        }

        /**
         * sets ping timeout in milliseconds
         *
         * @param timeout in milliseconds
         * @return Builder
         */
        fun timeout(timeout: Int): Builder {
            this.timeout = timeout
            return this
        }

        /**
         * sets HTTP response code indicating that connection is established
         *
         * @param httpResponse as integer
         * @return Builder
         */
        fun httpResponse(httpResponse: Int): Builder {
            this.httpResponse = httpResponse
            return this
        }

        /**
         * sets error handler for pings and connections
         *
         * @return Builder
         */
        fun errorHandler(errorHandler: ErrorHandler): Builder {
            this.errorHandler = errorHandler
            return this
        }

        /**
         * sets internet observing strategy
         *
         * @param strategy for observing and internet connection
         * @return Builder
         */
        fun strategy(strategy: InternetObservingStrategy): Builder {
            this.strategy = strategy
            return this
        }

        fun build(): InternetObservingSettings {
            return InternetObservingSettings(this)
        }

    }

    companion object {

        /**
         * @return settings with default parameters
         */
        fun create(): InternetObservingSettings {
            return Builder().build()
        }

        /**
         * Creates builder object
         * @return Builder
         */
        fun builder(): Builder {
            return Builder()
        }
    }

}

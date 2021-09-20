package org.quranacademy.quran.data.network.networkobserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Connectivity class represents current connectivity status. It wraps NetworkInfo object.
 */
class Connectivity private constructor(builder: Builder = builder()) {

    private val state: NetworkInfo.State // NOPMD
    private val detailedState: NetworkInfo.DetailedState? // NOPMD
    private val type: Int // NOPMD
    private val subType: Int // NOPMD
    private val available: Boolean // NOPMD
    private val failover: Boolean // NOPMD
    private val roaming: Boolean // NOPMD
    private val typeName: String // NOPMD
    private val subTypeName: String? // NOPMD
    private val reason: String? // NOPMD
    private val extraInfo: String? // NOPMD

    init {
        state = builder.state
        detailedState = builder.detailedState
        type = builder.type
        subType = builder.subType
        available = builder.available
        failover = builder.failover
        roaming = builder.roaming
        typeName = builder.typeName
        subTypeName = builder.subTypeName
        reason = builder.reason
        extraInfo = builder.extraInfo
    }

    fun state(): NetworkInfo.State {
        return state
    }

    fun detailedState(): NetworkInfo.DetailedState? {
        return detailedState
    }

    fun type(): Int {
        return type
    }

    fun subType(): Int {
        return subType
    }

    fun available(): Boolean {
        return available
    }

    fun failover(): Boolean {
        return failover
    }

    fun roaming(): Boolean {
        return roaming
    }

    fun typeName(): String {
        return typeName
    }

    fun subTypeName(): String? {
        return subTypeName
    }

    fun reason(): String? {
        return reason
    }

    fun extraInfo(): String? {
        return extraInfo
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as Connectivity?

        if (type != that!!.type) {
            return false
        }
        if (subType != that.subType) {
            return false
        }
        if (available != that.available) {
            return false
        }
        if (failover != that.failover) {
            return false
        }
        if (roaming != that.roaming) {
            return false
        }
        if (state != that.state) {
            return false
        }
        if (detailedState != that.detailedState) {
            return false
        }
        if (typeName != that.typeName) {
            return false
        }
        if (if (subTypeName != null) subTypeName != that.subTypeName else that.subTypeName != null) {
            return false
        }
        if (if (reason != null) reason != that.reason else that.reason != null) {
            return false
        }

        return if (extraInfo != null) extraInfo == that.extraInfo else that.extraInfo == null
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + (detailedState?.hashCode() ?: 0)
        result = 31 * result + type
        result = 31 * result + subType
        result = 31 * result + if (available) 1 else 0
        result = 31 * result + if (failover) 1 else 0
        result = 31 * result + if (roaming) 1 else 0
        result = 31 * result + typeName.hashCode()
        result = 31 * result + (subTypeName?.hashCode() ?: 0)
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (extraInfo?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("Connectivity{"
                + "state="
                + state
                + ", detailedState="
                + detailedState
                + ", type="
                + type
                + ", subType="
                + subType
                + ", available="
                + available
                + ", failover="
                + failover
                + ", roaming="
                + roaming
                + ", typeName='"
                + typeName
                + '\''.toString()
                + ", subTypeName='"
                + subTypeName
                + '\''.toString()
                + ", reason='"
                + reason
                + '\''.toString()
                + ", extraInfo='"
                + extraInfo
                + '\''.toString()
                + '}'.toString())
    }

    class Builder {

        // disabling PMD for builder class attributes
        // because we want to have the same method names as names of the attributes for builder

        var state: NetworkInfo.State = NetworkInfo.State.DISCONNECTED // NOPMD
        var detailedState: NetworkInfo.DetailedState = NetworkInfo.DetailedState.IDLE // NOPMD
        var type = UNKNOWN_TYPE // NOPMD
        var subType = UNKNOWN_SUB_TYPE // NOPMD
        var available = false // NOPMD
        var failover = false // NOPMD
        var roaming = false // NOPMD
        var typeName: String = "NONE" // NOPMD
        var subTypeName: String? = "NONE" // NOPMD
        var reason: String? = "" // NOPMD
        var extraInfo: String? = "" // NOPMD

        fun state(state: NetworkInfo.State): Builder {
            this.state = state
            return this
        }

        fun detailedState(detailedState: NetworkInfo.DetailedState): Builder {
            this.detailedState = detailedState
            return this
        }

        fun type(type: Int): Builder {
            this.type = type
            return this
        }

        fun subType(subType: Int): Builder {
            this.subType = subType
            return this
        }

        fun available(available: Boolean): Builder {
            this.available = available
            return this
        }

        fun failover(failover: Boolean): Builder {
            this.failover = failover
            return this
        }

        fun roaming(roaming: Boolean): Builder {
            this.roaming = roaming
            return this
        }

        fun typeName(name: String): Builder {
            this.typeName = name
            return this
        }

        fun subTypeName(subTypeName: String?): Builder {
            this.subTypeName = subTypeName
            return this
        }

        fun reason(reason: String?): Builder {
            this.reason = reason
            return this
        }

        fun extraInfo(extraInfo: String?): Builder {
            this.extraInfo = extraInfo
            return this
        }

        fun build(): Connectivity {
            return Connectivity(this)
        }
    }

    companion object {

        internal val UNKNOWN_TYPE = -1
        internal val UNKNOWN_SUB_TYPE = -1

        fun create(): Connectivity {
            return builder().build()
        }

        fun create(context: Context): Connectivity {
            Preconditions.checkNotNull(context, "context == null")
            return create(context, getConnectivityManager(context))
        }

        private fun getConnectivityManager(context: Context): ConnectivityManager {
            val service = Context.CONNECTIVITY_SERVICE
            return context.getSystemService(service) as ConnectivityManager
        }

        protected fun create(context: Context, manager: ConnectivityManager?): Connectivity {
            Preconditions.checkNotNull(context, "context == null")

            if (manager == null) {
                return create()
            }

            val networkInfo = manager.activeNetworkInfo
            return networkInfo?.let { create(it) } ?: create()
        }

        private fun create(networkInfo: NetworkInfo): Connectivity {
            return Builder()
                    .state(networkInfo.state)
                    .detailedState(networkInfo.detailedState)
                    .type(networkInfo.type)
                    .subType(networkInfo.subtype)
                    .available(networkInfo.isAvailable)
                    .failover(networkInfo.isFailover)
                    .roaming(networkInfo.isRoaming)
                    .typeName(networkInfo.typeName)
                    .subTypeName(networkInfo.subtypeName)
                    .reason(networkInfo.reason)
                    .extraInfo(networkInfo.extraInfo)
                    .build()
        }

        private fun builder(): Builder {
            return Connectivity.Builder()
        }

        fun state(state: NetworkInfo.State): Builder {
            return builder().state(state)
        }

        fun state(detailedState: NetworkInfo.DetailedState): Builder {
            return builder().detailedState(detailedState)
        }

        fun type(type: Int): Builder {
            return builder().type(type)
        }

        fun subType(subType: Int): Builder {
            return builder().subType(subType)
        }

        fun available(available: Boolean): Builder {
            return builder().available(available)
        }

        fun failover(failover: Boolean): Builder {
            return builder().failover(failover)
        }

        fun roaming(roaming: Boolean): Builder {
            return builder().roaming(roaming)
        }

        fun typeName(typeName: String): Builder {
            return builder().typeName(typeName)
        }

        fun subTypeName(subTypeName: String): Builder {
            return builder().subTypeName(subTypeName)
        }

        fun reason(reason: String): Builder {
            return builder().reason(reason)
        }

        fun extraInfo(extraInfo: String): Builder {
            return builder().extraInfo(extraInfo)
        }
    }
}

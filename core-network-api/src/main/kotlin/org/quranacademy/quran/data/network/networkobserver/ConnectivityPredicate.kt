package org.quranacademy.quran.data.network.networkobserver

import android.net.NetworkInfo

/**
 * ConnectivityPredicate is a class containing predefined methods,
 * which can be used for filtering flow of network connectivity
 */
object ConnectivityPredicate {

    /**
     * Filter, which returns true if at least one given state occurred
     *
     * @param states NetworkInfo.State, which can have one or more states
     * @return true if at least one given state occurred
     */
    fun hasState(connectivity: Connectivity, vararg states: NetworkInfo.State): Boolean {
        for (state in states) {
            if (connectivity.state() === state) {
                return true
            }
        }
        return false
    }

    /**
     * Filter, which returns true if at least one given type occurred
     *
     * @param types int, which can have one or more types
     * @return true if at least one given type occurred
     */
    fun hasType(connectivity: Connectivity, vararg types: Int): Boolean {
        val extendedTypes = appendUnknownNetworkTypeToTypes(types)
        for (type in extendedTypes) {
            if (connectivity.type() === type) {
                return true
            }
        }
        return false
    }

    /**
     * Returns network types from the input with additional unknown type,
     * what helps during connections filtering when device
     * is being disconnected from a specific network
     *
     * @param types of the network as an array of ints
     * @return types of the network with unknown type as an array of ints
     */
    internal fun appendUnknownNetworkTypeToTypes(types: IntArray): IntArray {
        var i = 0
        val extendedTypes = IntArray(types.size + 1)
        for (type in types) {
            extendedTypes[i] = type
            i++
        }
        extendedTypes[i] = Connectivity.UNKNOWN_TYPE
        return extendedTypes
    }

}

package org.quranacademy.quran.data.network.networkobserver.network.observing;

import android.content.Context;

import org.quranacademy.quran.data.network.networkobserver.Connectivity;

import kotlinx.coroutines.flow.Flow;

/**
 * Network observing strategy allows to implement different strategies for monitoring network
 * connectivity change. Network monitoring API may differ depending of specific Android version.
 */
public interface NetworkObservingStrategy {
    /**
     * Observes network connectivity
     *
     * @param context of the Activity or an Application
     * @return Observable representing stream of the network connectivity
     */
    Flow<Connectivity> observeNetworkConnectivity(final Context context);

    /**
     * Handles errors, which occurred during observing network connectivity
     *
     * @param message   to be processed
     * @param exception which was thrown
     */
    void onError(final String message, final Exception exception);
}

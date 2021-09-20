package org.quranacademy.quran.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class NetworkChecker @Inject constructor(private val context: Context) {

    val isConnected: Boolean
        @SuppressLint("MissingPermission")
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

}
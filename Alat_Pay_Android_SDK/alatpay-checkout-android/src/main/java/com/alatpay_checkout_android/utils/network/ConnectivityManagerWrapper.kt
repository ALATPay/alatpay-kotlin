/**  This is a wrapper taking off Connectivity Manager so as to easily
 * create a mock for testing since the Connectivity Manager Package is  private
 *  for android SDK
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.utils.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresApi

open class ConnectivityManagerWrapper(private val connectivityManager: ConnectivityManager) {

    fun getActiveNetworkInfo(): NetworkInfo? {
        return connectivityManager.activeNetworkInfo
    }

    fun isNetworkAvailable(): Boolean {
        val networkInfo = getActiveNetworkInfo()
        return networkInfo != null && networkInfo.isConnected
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getActiveNetwork(): Network? {
        return connectivityManager.activeNetwork
    }

    fun getNetworkCapabilities(network: Network?): NetworkCapabilities? {
        return connectivityManager.getNetworkCapabilities(network)
    }

    // Add other methods here as needed
}

/**  ALATPay Network broadcast receiver to detect
 *  for no internet connection
 *
 *  @param networkChangeCallBack
 *  @param
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.utils.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager

interface NetworkChangeCallBack {
    fun onNetworkChanged(isConnected: Boolean)
}

open class NetworkBroadcastReceiver constructor(
    private val networkChangeCallBack: NetworkChangeCallBack,
    private val cm: ConnectivityManagerWrapper?
) : BroadcastReceiver() {
    private var isProcessingNetworkChangeEvent = false

    constructor() : this(
        object : NetworkChangeCallBack {
            override fun onNetworkChanged(isConnected: Boolean) {
            }
        },
        cm = null
    )

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                // Check if internet connectivity is available
                if (isInternetAvailable(context)) {
                    // Internet connection is available, do something
                    if (!isProcessingNetworkChangeEvent) {
                        // Set the flag to true
                        isProcessingNetworkChangeEvent = true

                        if (isInternetSpeedFast()) {
                            networkChangeCallBack.onNetworkChanged(true)
                        } else {
                            networkChangeCallBack.onNetworkChanged(false)
                        }

                        // Set the flag back to false
                        isProcessingNetworkChangeEvent = false
                    }
                } else {
                    // No internet connection, do something
                    if (!isProcessingNetworkChangeEvent) {
                        // Set the flag to true
                        isProcessingNetworkChangeEvent = true
                        networkChangeCallBack.onNetworkChanged(false)
                        // Set the flag back to false
                        isProcessingNetworkChangeEvent = false
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            // set to false
            networkChangeCallBack.onNetworkChanged(false)
        }
    }

    private fun isInternetSpeedFast(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                val capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork())
                if (capabilities != null) {
                    val downloadSpeed: Int = capabilities.linkDownstreamBandwidthKbps
                    if (downloadSpeed < 2000) {
                        // Check if download speed is less than 5000 kbps or 5mbps
                        // Slow internet connection detected
                        return false
                    }
                }
            }
        } else {
            if (cm != null) {
                val activeNetwork = cm.getActiveNetworkInfo()
                if (activeNetwork != null) {
                    if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                        return when (activeNetwork.subtype) {
                            TelephonyManager.NETWORK_TYPE_GPRS,
                            TelephonyManager.NETWORK_TYPE_EDGE,
                            TelephonyManager.NETWORK_TYPE_CDMA,
                            TelephonyManager.NETWORK_TYPE_1xRTT,
                            TelephonyManager.NETWORK_TYPE_IDEN -> // Slow mobile network
                                false
                            else ->
                                // Fast mobile network
                                true
                        }
                    }
                }
            }
        }
        return true
    }

    private fun isInternetAvailable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork())
                return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    )
            }
        } else {
//            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val activeNetwork = cm.getActiveNetworkInfo()
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                        // connected to wifi or mobile data
                        return true
                    }
                }
            }
        }

        return false
    }
}

package com.alatpay_checkout_android.utils.network

import android.content.Context
import com.alatpay_checkout_android.utils.getConnectivityManager

object NetworkModule {

    fun provideConnectivityManagerWrapper(context: Context): ConnectivityManagerWrapper {
        return ConnectivityManagerWrapper(context.getConnectivityManager()!!)
    }
}

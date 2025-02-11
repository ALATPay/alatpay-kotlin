/**  AlatPayCheckoutViewModel for communicating
 * with AlatPayCheckoutActivity
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.ui

import androidx.lifecycle.ViewModel
import com.alatpay_checkout_android.BuildConfig
import com.alatpay_checkout_android.data.models.ALATPayCheckoutParcel
import com.alatpay_checkout_android.data.models.CheckoutUrlUiState
import com.alatpay_checkout_android.data.models.NetworkUiState
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ALATPayCheckoutViewModel constructor() : ViewModel() {

    private val _uiNetworkState = MutableStateFlow(NetworkUiState())

    val uiNetworkState: StateFlow<NetworkUiState> = _uiNetworkState.asStateFlow()

    private val _uiCheckoutUrlState = MutableStateFlow(CheckoutUrlUiState())

    val uiCheckoutUrlState: StateFlow<CheckoutUrlUiState> = _uiCheckoutUrlState.asStateFlow()

    private val _uiShowDialogState = MutableStateFlow(false)

    val uiShowDialogState = _uiShowDialogState.asStateFlow()

    fun updateNetworkStatus(isConnected: Boolean) {
        _uiNetworkState.value = NetworkUiState(networkState = isConnected)
    }

    fun updateCheckoutUrl(checkout: ALATPayCheckoutParcel) {
        _uiCheckoutUrlState.value = CheckoutUrlUiState(
            url = when(checkout.environment){
                ALATPayConstants.Environment.DEV -> BuildConfig.DEV_CHECKOUT_URL
                ALATPayConstants.Environment.STAGING -> BuildConfig.STAGING_CHECKOUT_URL
                ALATPayConstants.Environment.PROD -> BuildConfig.PROD_CHECKOUT_URL
            },
//            isProdEnv = checkout.isProdEnv,
            parcelData = checkout
        )
    }

    fun showDialog(show: Boolean) {
        _uiShowDialogState.value = show
    }
}

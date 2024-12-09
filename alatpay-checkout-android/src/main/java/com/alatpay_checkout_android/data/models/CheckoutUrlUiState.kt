/**   AlatPay Checkout CheckoutUrlUiState Model
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.data.models

data class CheckoutUrlUiState(
    val url: String = "",
    val isProdEnv: Boolean = false,
    val parcelData: ALATPayCheckoutParcel  = ALATPayCheckoutParcel.default
)

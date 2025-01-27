package com.alatpay_checkout_android.data.models

import com.alatpay_checkout_android.utils.constants.ALATPayConstants

@kotlinx.serialization.Serializable
data class ALATPayCheckout(
    val amount: Double,
    val apiKey: String,
    val businessId: String,
    val currencyCode: String = ALATPayConstants.Currency.NGN.currencyName,
    val customerPhone: String,
    val customerEmail: String,
    val customerFirstName: String,
    val customerLastName: String,
    val reference: String,
    val environment: ALATPayConstants.Environment,
    val themeColor: String
)

package com.alatpay_checkout_android.utils.constants

import androidx.annotation.Keep

@Keep
object ALATPayConstants {
    @Keep
    enum class AlatPayTransactionStatus @Keep constructor(name: String) {
        SUCCESS("success"),
        FAILED("failed"),
        ABORTED("aborted");

        internal var statusName: String = name
    }
    @Keep
    enum class Environment @Keep constructor(name: String){
        PROD("prod"),
        STAGING("staging"),
        DEV("dev")
    }
    @Keep
    object IntentParams {
        const val CHECKOUT_PARAMS = "Checkout Params"
        const val ALAT_PAY_RESULT = "Transaction Result"
    }
    @Keep
    enum class Currency @Keep constructor(name: String) {
        GBP("GBP"),
        NGN("NGN");
//        USD("USD"),
//        CAD("CAD"),
//        EUR("EUR"),
//        UGX("UGX"),
//        INR("INR"),
//        XOF("XOF"),
//        CNY("CNY"),
//        TMT("TMT"),
//        JPY("JPY"),
//        HKD("HKD"),
//        AED("AED"),
//        SGD("SGD"),
//        TZS("TZS"),
//        AUD("AUD"),
//        CHF("CHF"),
//        PHP("PHP"),
//        XAF("XAF");

        internal var currencyName: String = name

        private fun checkCurrencyNameExists(name: String): Boolean {
            for (day in values()) {
                if (day.currencyName.equals(name, false)) {
                    return true
                }
            }

            return false
        }
    }
}
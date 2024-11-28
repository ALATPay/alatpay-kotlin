package com.alatpay_checkout_android.utils.constants

object Constants {
    enum class AlatPayApplicationMode(name: String) {
        MOBILE("mobile");

        internal var applicationModeName: String = name
    }
    object Error {
        const val CONTEXT_REQUIRED = "Context is Required"
        const val BROWSER_REQUIRED = "Browser is Required"

        const val INVALID_AMOUNT = "Amount is Invalid"
        const val API_KEY_REQUIRED = "API KEY is Required"
        const val INVALID_CUSTOMER_FIRST_NAME = "Customer First Name is Invalid"
        const val INVALID_CUSTOMER_LAST_NAME = "Customer Last Name is Invalid"
        const val INVALID_CUSTOMER_EMAIL = "Customer Email is Invalid"
        const val CUSTOMER_EMAIL_REQUIRED = "Customer Email is Required"
        const val REFERENCE_REQUIRED = "Reference is Required"
        const val BUSINESS_ID_REQUIRED = "Business Id is Required"
        const val TRANSACTION_ABORTED = "Transaction Aborted"
    }

    object RegexPatterns {

        const val NAME_PATTERN = "^[\\p{L}0-9 .'&-_]+$"
        const val EMAIL_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    }
}
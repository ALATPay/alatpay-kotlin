/**   ALATPay Checkout validators before initializing the SDK
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.utils

import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import com.alatpay_checkout_android.utils.constants.Constants.RegexPatterns.EMAIL_PATTERN
import com.alatpay_checkout_android.utils.constants.Constants.RegexPatterns.NAME_PATTERN


object TxnCheckoutValidators {
    fun validateAmount(amount: Double): Boolean {
        if (amount < 1.0) {
            return false
        }

        return true
    }

    fun validateCurrencyCode(currency: String?): Boolean {
        if (currency.isNullOrEmpty()) {
            return false
        }

        if (!checkIfCurrencyExists(currency)) {
            return false
        }

        return true
    }

    private fun checkIfCurrencyExists(currency: String): Boolean {
        for (cur in ALATPayConstants.Currency.values()) {
            if (cur.currencyName.equals(currency, false)) {
                return true
            }
        }
        return false
    }

//    fun validateCustomerName(name: String, isFieldOptional: Boolean): Boolean {
//        if (!isFieldOptional && name.isEmpty()) {
//            return false
//        }
//
//        if (!isFieldOptional && isCustomerNameValid(name)) {
//            return false
//        }
//
//        if (isFieldOptional && name.isNotEmpty() && isCustomerNameValid(name)) {
//            return false
//        }
//
//        return true
//    }

    fun validateCustomerFirstName(name: String,): Boolean {
        if (name.isEmpty()) {
            return false
        }

        if (isCustomerNameValid(name)) {
            return false
        }

        if (name.isNotEmpty() && isCustomerNameValid(name)) {
            return false
        }

        return true
    }
    fun validateCustomerLastName(name: String,): Boolean {
        if (name.isEmpty()) {
            return false
        }

        if (isCustomerNameValid(name)) {
            return false
        }

        if (name.isNotEmpty() && isCustomerNameValid(name)) {
            return false
        }

        return true
    }

    private fun isCustomerNameValid(name: String): Boolean {
        if (!Regex(NAME_PATTERN).matches(name)) {
            return true
        }
        return false
    }

    fun validateCustomerEmail(email: String,): Boolean {
        if ( email.isEmpty()) {
            return false
        }

        if ( isCustomerEmailValid(email)) {
            return false
        }

        if (email.isNotEmpty() && isCustomerEmailValid(email)) {
            return false
        }

        return true
    }

    private fun isCustomerEmailValid(email: String): Boolean {
        if (!Regex(EMAIL_PATTERN).matches(email)) {
            return true
        }

        return false
    }

    fun validateApiKey(apiKey: String): Boolean {
        if (apiKey.isEmpty()) {
            return false
        }

        return true
    }




    fun validateReference(reference: String): Boolean {
        if (reference.isEmpty()) {
            return false
        }

        return true
    }
}

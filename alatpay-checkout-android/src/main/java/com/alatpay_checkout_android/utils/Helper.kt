/**   ALATPay SDK Library helper extensions
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.alatpay_checkout_android.BuildConfig
import com.alatpay_checkout_android.data.models.ALATPayCheckout
import com.alatpay_checkout_android.data.models.ALATPayCheckoutParcel
import com.alatpay_checkout_android.ui.ALATPayCheckoutActivity
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import com.alatpay_checkout_android.utils.constants.Constants
import java.util.regex.Pattern

internal fun Context?.isContextNotNull(): Boolean {
    return this != null
}

internal fun Context.getConnectivityManager(): ConnectivityManager? {
    return getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
}

internal fun ALATPayCheckoutParcel.toSerialize(): ALATPayCheckout {
    return ALATPayCheckout(
        amount = amount,
        currencyCode = currencyCode,
        apiKey = key,
        customerEmail = customerEmail,
        customerFirstName = customerFirstName,
        customerLastName = customerLastName,
        reference = reference,
        environment = environment,
        businessId = businessId,
        customerPhone = customerPhone,
        themeColor = themeColor
    )
}


internal fun ALATPayConstants.Environment.getEnvironment(): String{
   return when(this){
       ALATPayConstants.Environment.PROD -> BuildConfig.PROD_CHECKOUT_URL
       ALATPayConstants.Environment.STAGING -> BuildConfig.STAGING_CHECKOUT_URL
       ALATPayConstants.Environment.DEV -> BuildConfig.DEV_CHECKOUT_URL
        else -> BuildConfig.PROD_CHECKOUT_URL
    }
}






internal fun validateRequest(
    alatPayCheckout: ALATPayCheckoutParcel,
    onInitializeError: (message: String) -> Unit
): Boolean {
    if (!TxnCheckoutValidators.validateAmount(amount = alatPayCheckout.amount)) {
        onInitializeError(Constants.Error.INVALID_AMOUNT)
        return true
    }

//    if (!CheckoutValidators.validateCurrencyCode(currency = alatPayCheckout.currencyCode?.currencyName)) {
//        onInitializeError(Constants.Error.INVALID_CURRENCY)
//        return true
//    }

    if (!TxnCheckoutValidators.validateApiKey(apiKey = alatPayCheckout.key)) {
        onInitializeError(Constants.Error.API_KEY_REQUIRED)
        return true
    }


    if (!TxnCheckoutValidators.validateCustomerFirstName(name = alatPayCheckout.customerFirstName,)) {
        onInitializeError(Constants.Error.INVALID_CUSTOMER_FIRST_NAME)
        return true
    }

    if(alatPayCheckout.themeColor.isNotEmpty() && !TxnCheckoutValidators.isValidHexColor(hex = alatPayCheckout.themeColor) ){
        onInitializeError(Constants.Error.INVALID_HEX_COLOR_CODE)
        return true
    }

    if (!TxnCheckoutValidators.validateCustomerLastName(name = alatPayCheckout.customerLastName,)) {
        onInitializeError(Constants.Error.INVALID_CUSTOMER_LAST_NAME)
        return true
    }

    if (!TxnCheckoutValidators.validateCustomerEmail(email = alatPayCheckout.customerEmail)) {
        onInitializeError(Constants.Error.INVALID_CUSTOMER_EMAIL)
        return true
    }

    if (!TxnCheckoutValidators.validateReference(reference = alatPayCheckout.reference)) {
        onInitializeError(Constants.Error.REFERENCE_REQUIRED)
        return true
    }

    return false
}

internal fun hasWebBrowser(context: Context): Boolean {
    val packageManager = context.packageManager

    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))

    val activities: List<ResolveInfo> = packageManager.queryIntentActivities(browserIntent, 0)

    return activities.isEmpty()
}

internal fun goToPaymentView(
    context: Context,
    startActivityResult: ActivityResultLauncher<Intent>,
    alatPayCheckout: ALATPayCheckoutParcel
) {
    val intent = Intent(context, ALATPayCheckoutActivity::class.java)
    intent.putExtra(ALATPayConstants.IntentParams.CHECKOUT_PARAMS, alatPayCheckout)

    startActivityResult.launch(intent)
}

fun isBrowsableUrl(url: String): Boolean {
    // Regex for checking a browsable URL
    val regex = "^(https?://)" + // Must start with http:// or https://
            "(([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6})" + // Domain
            "(:\\d{1,5})?" + // Optional port
            "(/.*)?$" // Optional path/query

    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(url)
    return matcher.matches()
}

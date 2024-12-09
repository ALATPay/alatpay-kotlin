/**  ALATPay SDK Manager to initialize payment
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.alatpay_checkout_android.data.models.ALATPayCheckoutParcel
import com.alatpay_checkout_android.utils.constants.Constants.Error.CONTEXT_REQUIRED

object TxnCheckoutManager {

    fun initialize(
        context: Context?,
        alatPayCheckout: ALATPayCheckoutParcel,
        startActivityResult: ActivityResultLauncher<Intent>,
        onInitializeError: (message: String) -> Unit
    ) {
        if (validateRequest(
                alatPayCheckout,
                onInitializeError = {
                    onInitializeError(it)
                }
            )
        ) {
            return
        }

        // check if context is not null
        if (context != null && context.isContextNotNull()) {
//            if (hasWebBrowser(context)) {
//                onInitializeError(BROWSER_REQUIRED)
//                return
//            }
            goToPaymentView(
                context,
                startActivityResult,
                alatPayCheckout
            )
        } else {
            onInitializeError(
                CONTEXT_REQUIRED
            )
            return
        }
    }
}

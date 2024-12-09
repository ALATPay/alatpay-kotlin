/**   AlatPay LeatherBackTransactionResponse to be returned
 *  to merchant after a transaction
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.data.models

import android.os.Parcelable
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import kotlinx.parcelize.Parcelize

@Parcelize
data class ALATPayTransactionResponse(
    val status: ALATPayConstants.AlatPayTransactionStatus,
    val message: String,
    val transactionPayload: String
) : Parcelable

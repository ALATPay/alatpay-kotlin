/**   AlatPay LeatherBackTransactionResponse to be returned
 *  to merchant after a transaction
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.data.models

import android.os.Parcelable
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class ALATPayTransactionResponse(
    val status: ALATPayConstants.AlatPayTransactionStatus,
    val message: String,
    val transactionPayload: String
) : Parcelable

@Serializable
data class TransactionResponse(
    val data: TransactionData,
    val status: Boolean,
    val message: String,
    val statusCode: Int
)

@Serializable
data class TransactionData(
    val amount: Int,
    val orderId: String,
    val description: String?,
    val paymentMethodId: Int,
    val sessionId: String,
    val isAmountDiscrepant: Boolean,
    val amountSent: Int,
    val nipTransaction: NipTransaction,
    val virtualAccount: VirtualAccount,
    val customer: Customer,
    val id: String,
    val merchantId: String,
    val businessId: String,
    val channel: String?,
    val callbackUrl: String,
    val feeAmount: Int,
    val businessName: String,
    val currency: String,
    val status: String,
    val statusReason: String?,
    val settlementType: String,
    val createdAt: String,
    val updatedAt: String,
    val ngnVirtualBankAccountNumber: String?,
    val ngnVirtualBankCode: String?,
    val usdVirtualAccountNumber: String?,
    val usdVirtualBankCode: String?
)
@Serializable
data class NipTransaction(
    val id: String,
    val requestdate: String?,
    val nibssresponse: String?,
    val sendstatus: String?,
    val sendresponse: String?,
    val transactionId: String,
    val transactionStatus: String,
    val log: String?,
    val createdAt: String,
    val originatoraccountnumber: String,
    val originatorname: String,
    val bankname: String,
    val bankcode: String,
    val amount: Int,
    val narration: String,
    val craccountname: String,
    val craccount: String,
    val paymentreference: String,
    val sessionid: String
)
@Serializable
data class VirtualAccount(
    val id: String,
    val merchantId: String,
    val virtualBankCode: String,
    val virtualBankAccountNumber: String,
    val businessBankAccountNumber: String,
    val businessBankCode: String,
    val transactionId: String,
    val status: String,
    val expiredAt: String,
    val settlementType: String,
    val createdAt: String,
    val businessId: String,
    val amount: Int,
    val currency: String,
    val orderId: String,
    val description: String?,
    val customer: String?
)
@Serializable
data class Customer(
    val id: String,
    val transactionId: String,
    val createdAt: String,
    val email: String,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val metadata: String?
)


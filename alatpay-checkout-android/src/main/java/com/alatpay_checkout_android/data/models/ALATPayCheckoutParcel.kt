package com.alatpay_checkout_android.data.models
import android.os.Parcelable
import androidx.annotation.Keep
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import kotlinx.parcelize.Parcelize

@Parcelize
data class ALATPayCheckoutParcel(
    val amount: Double,
    val key: String,
    val businessId: String,
    val currencyCode: String = ALATPayConstants.Currency.NGN.currencyName,
    val customerPhone: String,
    val customerEmail: String,
    val customerFirstName: String,
    val customerLastName: String,
    val reference: String,
    val isProdEnv: Boolean,
    val environment: ALATPayConstants.Environment,
    val themeColor : String,
): Parcelable{
    companion object{
        val default = ALATPayCheckoutParcel(
            amount = 0.0,
            key = "",
            businessId = "",
            customerPhone = "",
            customerEmail = "",
            customerFirstName = "",
            customerLastName = "",
            reference = "",
            isProdEnv = false,
            environment = ALATPayConstants.Environment.DEV,
            themeColor = ""
        )
    }

    @Keep
    data class Builder(
        var amount: Double = 0.0,
        var apiKey: String = "",
        var businessId: String ="",
        var currencyCode: String = ALATPayConstants.Currency.NGN.currencyName,
        var customerPhone: String = "",
        var customerEmail: String = "",
        var customerFirstName: String = "",
        var customerLastName: String = "",
        var reference: String = "",
        var isProdEnv: Boolean = false,
        var environment: ALATPayConstants.Environment = ALATPayConstants.Environment.DEV,
        var themeColor: String = ""
    ){
        fun setAmount(amount: Double) = apply { this.amount = amount }
        fun setCurrencyCode(currencyCode: ALATPayConstants.Currency) = apply { this.currencyCode = currencyCode.currencyName }
        fun setApiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun setCustomerEmail(customerEmail: String) = apply { this.customerEmail = customerEmail }
        fun setCustomerPhone(customerPhone: String) = apply { this.customerPhone = customerPhone }
        fun setBusinessId(businessId: String) = apply { this.businessId = businessId }
        fun setCustomerFirsName(customerFirstName: String) = apply { this.customerFirstName = customerFirstName }
        fun setCustomerLastName(customerLastName: String) = apply { this.customerLastName = customerLastName }
        fun setReference(reference: String) = apply { this.reference = reference }
        fun setIsProdEnv(isProdEnv: Boolean) = apply { this.isProdEnv = isProdEnv }
        fun setEnvironment(env: ALATPayConstants.Environment) = apply { this.environment = env }
        fun setThemeColor(themeColor: String) = apply { this.themeColor = themeColor }

        fun build() = ALATPayCheckoutParcel(
            amount = amount,
            currencyCode = currencyCode,
            key = apiKey,
            customerEmail = customerEmail,
            customerFirstName = customerFirstName,
            customerLastName = customerLastName,
            reference = reference,
            isProdEnv = isProdEnv,
            environment = environment,
            businessId = businessId,
            customerPhone = customerPhone,
            themeColor = themeColor
        )
    }
}

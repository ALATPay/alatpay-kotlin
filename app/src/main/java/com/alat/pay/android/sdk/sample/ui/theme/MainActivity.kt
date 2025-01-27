package com.alat.pay.android.sdk.sample.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alat.pay.android.sdk.sample.R
import com.alatpay_checkout_android.data.models.ALATPayCheckoutParcel
import com.alatpay_checkout_android.data.models.ALATPayTransactionResponse
import com.alatpay_checkout_android.utils.TxnCheckoutManager
import com.alatpay_checkout_android.utils.constants.ALATPayConstants

class MainActivity : ComponentActivity() {
    //Result Launcher and receiver
    private val startActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it.data?.getParcelableExtra(
                ALATPayConstants.IntentParams.ALAT_PAY_RESULT,
                ALATPayTransactionResponse::class.java
            )
        } else {
            it.data?.getParcelableExtra(
                ALATPayConstants.IntentParams.ALAT_PAY_RESULT
            )
        }

        if (it.resultCode == RESULT_OK && data != null) {

            when (data.status) {
                ALATPayConstants.AlatPayTransactionStatus.SUCCESS -> {
//                    println(data.status)
                    println("Success Payload: ${data.transactionPayload}" )
//                    println(data.referenceId)
                }

                ALATPayConstants.AlatPayTransactionStatus.FAILED -> {
//                    println(data.status)
//                    println(data.message)
//                    println(data.referenceId)
                }
                ALATPayConstants.AlatPayTransactionStatus.ABORTED -> {
//                    println(data.status)
//                    println(data.message)
//                    println(data.referenceId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Alat_Pay_Android_SDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(verticalArrangement = Arrangement.Center){
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                        val context = LocalContext.current
                        Button(
                            onClick = {
                                initiateSamplePayment(context)
                            }
                        ) {
                            Text("Make  Payment")
                        }
                    }
                }
            }
        }
    }
    private fun initiateSamplePayment(context: Context) {
        val ref = generateRandomString(16)
        ref.run {
//            println(this)
            val alatPayCheckout = ALATPayCheckoutParcel
                .Builder()
                .setAmount(100.toDouble())
//                .setApiKey("4401dcd8e06c4c768d2e30619767e86b")
//                .setBusinessId("e3bdb74b-2076-4b24-6295-08db0815ccca")
                .setApiKey("4616217a2647473cabdcccb084531f4b")
                .setBusinessId("2a3112a6-d303-4bb8-5921-08dcdbbb654a")
                .setEnvironment(ALATPayConstants.Environment.PROD)
                .setCustomerFirsName("Seun")
                .setCustomerLastName("Ajibade")
                .setThemeColor("#ca489e")
                .setCustomerEmail("ajibadeseun@gmail.com")
                .setCurrencyCode(ALATPayConstants.Currency.NGN)
                .setReference(ref)
                .build()



            TxnCheckoutManager.initialize(
                context,
                alatPayCheckout,
                startActivityResult,
                onInitializeError = {
                    Toast.makeText(context,it,Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Alat_Pay_Android_SDKTheme {
            Greeting("Android")
        }
    }
}
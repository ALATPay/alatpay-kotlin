# ALATPay Kotlin SDK
A simple and efficient way to integrate ALATPay into your Android applications. Easily accept payments and manage transactions.


## Test Data
Use the following test credentials to perform live transactions:

- **Live Test Business ID**: `your_bussiness_id`
- **Live API Key**: `your_api_key`

**Note:** This will debit your real account.

## Importing the AAR in Your Sample Project
Follow these steps to integrate the ALATPay Kotlin SDK:

1. Add the JitPack repository to your build file

// Add it in your settings.gradle.kts at the end of repositories: 
```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```


2. Add the dependency:

```
dependencies {
	        implementation("com.github.ALATPay:alatpay-kotlin:1.0.0")
	}
```

## Implementation Guide
Add the following code to your **MainActivity**:

```kotlin
class MainActivity : ComponentActivity() {

    // Result Launcher and Receiver
    private val startActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it.data?.getParcelableExtra(
                ALATPayConstants.IntentParams.ALAT_PAY_RESULT,
                ALATPayTransactionResponse::class.java
            )
        } else {
            it.data?.getParcelableExtra(ALATPayConstants.IntentParams.ALAT_PAY_RESULT)
        }

        if (it.resultCode == RESULT_OK && data != null) {
            when (data.status) {
                ALATPayConstants.AlatPayTransactionStatus.SUCCESS -> {
                    println("Success Payload: ${data.transactionPayload}")
                }
                ALATPayConstants.AlatPayTransactionStatus.FAILED -> {
                    println("Transaction Failed: ${data.message}")
                }
                ALATPayConstants.AlatPayTransactionStatus.ABORTED -> {
                    println("Transaction Aborted: ${data.message}")
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
                    Column(verticalArrangement = Arrangement.Center) {
                        Greeting(name = "Android", modifier = Modifier.padding(innerPadding))
                        val context = LocalContext.current
                        Button(
                            onClick = {
                                initiateSamplePayment(context)
                            }
                        ) {
                            Text("Make Payment")
                        }
                    }
                }
            }
        }
    }

    private fun initiateSamplePayment(context: Context) {
        val ref = generateRandomString(16)
        ref.run {
            val alatPayCheckout = ALATPayCheckoutParcel
                .Builder()
                .setAmount(100.toDouble())
                .setApiKey("4616217a2647473cabdcccb084531f4b")
                .setBusinessId("2a3112a6-d303-4bb8-5921-08dcdbbb654a")
                .setEnvironment(ALATPayConstants.Environment.PROD)
                .setCustomerFirsName("Seun")
                .setCustomerLastName("Ajibade")
                .setCustomerEmail("lunguboy@gmail.com")
                .setCurrencyCode(ALATPayConstants.Currency.NGN)
                .setReference(ref)
                .build()

            TxnCheckoutManager.initialize(
                context,
                alatPayCheckout,
                startActivityResult,
                onInitializeError = {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Alat_Pay_Android_SDKTheme {
        Greeting("Android")
    }
}
```

## Notes
- Ensure that you have **Internet permission** in your `AndroidManifest.xml`.
- Always use the correct **API Key** and **Business ID** for transactions.

## License
This SDK is provided under the **MIT License**.

/**   ALATPay Checkout using Android WebView
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.ui

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alatpay_checkout_android.R
import com.alatpay_checkout_android.data.models.ALATPayCheckoutParcel
import com.alatpay_checkout_android.data.models.ALATPayTransactionResponse
import com.alatpay_checkout_android.ui.txnCheckoutWebView.CheckoutWebView
import com.alatpay_checkout_android.ui.common.AnimatedLoader
import com.alatpay_checkout_android.ui.common.CustomAlertDialog
import com.alatpay_checkout_android.ui.common.DiagonalDebugLabel
import com.alatpay_checkout_android.ui.common.ErrorScreen
import com.alatpay_checkout_android.ui.common.NetworkBadge
import com.alatpay_checkout_android.ui.theme.AlatPayCheckoutTheme
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import com.alatpay_checkout_android.utils.constants.ALATPayConstants.IntentParams.ALAT_PAY_RESULT
import com.alatpay_checkout_android.utils.constants.ALATPayConstants.IntentParams.CHECKOUT_PARAMS
import com.alatpay_checkout_android.utils.constants.Constants.Error.TRANSACTION_ABORTED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ALATPayCheckoutActivity : ComponentActivity() {
    var webView: WebView? = null
    private val viewModel: ALATPayCheckoutViewModel by viewModels()

    private var data: ALATPayCheckoutParcel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CHECKOUT_PARAMS, ALATPayCheckoutParcel::class.java)
        } else {
            intent.getParcelableExtra(CHECKOUT_PARAMS)
        }

        data?.let { viewModel.updateCheckoutUrl(it) }

        setContent {
            AlatPayCheckout()
        }
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
    }

    override fun onPause() {
        super.onPause()

        // Unregister the receiver when the activity is paused
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AlatPayCheckout() {
        AlatPayCheckoutTheme {
            val state = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
            val sheet = rememberBottomSheetScaffoldState(bottomSheetState = state)

            val sheetState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.Hidden,
                    skipHiddenState = false
                )
            )


            val networkUiState by viewModel.uiNetworkState.collectAsState()
            val scope = rememberCoroutineScope()

            var hasLoaded by remember {
                mutableStateOf(false)
            }

            var showNetworkBadge by remember { mutableStateOf(false) }

            LaunchedEffect(networkUiState.networkState) {
                if (!networkUiState.networkState) {
                    // Show the badge for 2 seconds
                    showNetworkBadge = true
                    delay(5000)
                    showNetworkBadge = false
                }
            }

            var hasPageFinished by remember {
                mutableStateOf(false)
            }
            androidx.compose.material3.BottomSheetScaffold(
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    Box(
                        modifier = Modifier.fillMaxSize()
//                            .fillMaxHeight(1f)
//                            .fillMaxWidth()
                    ) {
                        ErrorScreen(
                            title = stringResource(id = R.string.connection_failed),
                            msg = stringResource(id = R.string.connection_failed_msg),
                            onTryAgainClicked = {
                                redirectBackToApp(
                                    ALATPayConstants.AlatPayTransactionStatus.ABORTED,
                                    "",
                                    TRANSACTION_ABORTED
                                )
                            },
                            onCancelClicked = {
                                viewModel.showDialog(!viewModel.uiShowDialogState.value)
                            }
                        )
                    }
                },
                scaffoldState = sheetState
            ) {
                ScreenContent(
                    modifier = Modifier.fillMaxSize()
//                        .fillMaxHeight(1f)
                    ,
                    networkState = networkUiState.networkState,
                    hasLoaded = hasLoaded,
                    showNetworkBadge = showNetworkBadge,
                    hasPageFinished = hasPageFinished,
                    onHasPageFinished = {
                        hasPageFinished = it
                    },
                    onHasLoaded = {
                        hasLoaded = it
                    },
                    onReload = {
                        scope.launch {
                            if (state.isCollapsed) {
                                state.expand()
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun ScreenContent(
        modifier: Modifier = Modifier,
        networkState: Boolean,
        showNetworkBadge: Boolean = false,
        hasLoaded: Boolean,
        hasPageFinished: Boolean,
        onHasPageFinished: (Boolean) -> Unit,
        onHasLoaded: (Boolean) -> Unit,
        onReload: () -> Unit
    ) {
        val checkoutUiState by viewModel.uiCheckoutUrlState.collectAsState()
        val showDialogState by viewModel.uiShowDialogState.collectAsState()

        var backEnabled by remember { mutableStateOf(false) }

        Box(modifier = modifier) {
            CheckoutWebView(
                checkoutUrl = checkoutUiState.url,
                networkState = networkState,
                hasPageFinished = hasPageFinished,
                onHasPageFinished = onHasPageFinished,
                onSetWebView = {
                    webView = it
                },
                onBackEnabled = {
                    backEnabled = it
                },
                onResult = { status, transactionPayload, message ->
                    redirectBackToApp(
                        status = status,
                        transactionPayload = transactionPayload ?: "",
                        message = message ?: ""
                    )
                },
                onHasPageLoaded = {
                    onHasLoaded(it)
                },
                onReload = onReload,
                checkoutData = data?:ALATPayCheckoutParcel.default,

            )

            if (!hasLoaded) {
                AnimatedLoader()
            }

            data?.let {
                if (it.environment != ALATPayConstants.Environment.PROD) {
                    DiagonalDebugLabel()
                }
            }

            NetworkBadge(
                showNetworkBadge = showNetworkBadge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.TopCenter)
            )

            CustomAlertDialog(
                isVisible = showDialogState,
                onCancelClicked = {
                    viewModel.showDialog(false)
                },
                onConfirmClicked = {
                    redirectBackToApp(
                        ALATPayConstants.AlatPayTransactionStatus.ABORTED,
                        "",
                        TRANSACTION_ABORTED
                    )
                }
            )
        }

        BackHandler(enabled = backEnabled) {
            if (networkState) {
                webView?.goBack()
            }
        }
    }

    private fun redirectBackToApp(
        status: ALATPayConstants.AlatPayTransactionStatus,
        transactionPayload: String,
        message: String
    ) {
        val resultIntent = Intent()
        resultIntent.putExtra(
            ALAT_PAY_RESULT,
            ALATPayTransactionResponse(
                status = status,
                transactionPayload = transactionPayload,
                message = message
            )
        )
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AlatPayCheckout()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // add stuff here
        viewModel.showDialog(!viewModel.uiShowDialogState.value)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (webView != null) {
            // Clear the cache
            webView!!.clearCache(true)

            // Stop loading any content
            webView!!.stopLoading()

            // Remove any references to the WebView
            webView!!.webChromeClient = null

            // Destroy the WebView
            webView!!.destroy()
        }
    }
}

/**
 *  WebView Composable for AlatPay checkout
 * @param checkoutUrl as String
 * @param onResult callback url that returns custom response (Add Class name here: )
 *
 *
 * N:B: Since Webview Composable has not been created by google at the time the code was written,
 *      have to make use of AndroidView to create the xml Webview
 *
 *
 * * * * @author by Seun Ajibade
 * **/

package com.alatpay_checkout_android.ui.txnCheckoutWebView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import com.alatpay_checkout_android.R
import com.alatpay_checkout_android.data.models.ALATPayCheckoutParcel
import com.alatpay_checkout_android.data.models.TransactionResponse
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import com.alatpay_checkout_android.utils.getBaseUrl
import com.alatpay_checkout_android.utils.getEnvironment
import com.google.gson.Gson


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CheckoutWebView(
    checkoutUrl: String,
    networkState: Boolean = true,
    checkoutData: ALATPayCheckoutParcel,
    onSetWebView: (WebView) -> Unit,
    onBackEnabled: (Boolean) -> Unit,
    onResult: (
        status: ALATPayConstants.AlatPayTransactionStatus,
        transactionPayload: String?,
        message: String?
    ) -> Unit,
    onReload: () -> Unit,
    hasPageFinished: Boolean,
    onHasPageFinished: (Boolean) -> Unit,
    onHasPageLoaded: (state: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentDesc = LocalContext.current.resources.getString(R.string.alat_pay_webview)
    val coroutineScope = rememberCoroutineScope()

    var errorGot by remember { mutableStateOf(false) }
    var newWindowResult: Message? by remember { mutableStateOf(null) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val scaleFactor = if (configuration.screenHeightDp > configuration.screenWidthDp) {
        configuration.screenHeightDp.toFloat() / 1000
    } else {
        configuration.screenWidthDp.toFloat() / 1000
    }
    val result = (density.density * scaleFactor * 109).toInt()

    // States to manage width and height dynamically
    var webViewWidth by remember { mutableStateOf(0) }
    var webViewHeight by remember { mutableStateOf(0) }

    BackHandler {
        onBackEnabled(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = contentDesc }
    ) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
//                    layoutParams = FrameLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                    ).apply {
//                        gravity = Gravity.CENTER
//                    }

//                    setInitialScale(1)
//                    setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY)
//                    setScrollbarFadingEnabled(false)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        supportMultipleWindows()
                        javaScriptCanOpenWindowsAutomatically = true
                        allowFileAccess = true
                        allowContentAccess = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//                        userAgentString =
//                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"


                    }

                    // Referrer Policy
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        settings.safeBrowsingEnabled = true
                    }

                    // Enable debugging for testing headers and CORS
//                    WebView.setWebContentsDebuggingEnabled(true)

                    // Set WebChromeClient for handling JavaScript dialogs and logs
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d("WebViewConsole", consoleMessage?.message() ?: "Unknown message")
                            return super.onConsoleMessage(consoleMessage)
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                            errorGot = false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val newUrl = request?.url.toString()
                            println("🔍 WebView URL Changed: $newUrl")  // Debug log
                            if (newUrl.startsWith("data:")) {
                                // Load the data URI
                                loadDataWithBaseURL(null, newUrl, "text/html", "UTF-8", null)

                            }
                            return super.shouldOverrideUrlLoading(view, request)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            onHasPageFinished(true)
                            onHasPageLoaded(true)

//                            setInitialScale(result)


                        }

                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            errorGot = errorCode == -2
                            super.onReceivedError(view, errorCode, description, failingUrl)
                        }

                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onReceivedError(
                            view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                        ) {
                            errorGot = error?.errorCode == -2
                            super.onReceivedError(view, request, error)
                        }
                    }

                    // Add JavaScript interface for callbacks
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onTransaction(response: String) {
                            Log.d("WebViewResponse", "Payload: $response")
                            val transactionResponse =
                                Gson().fromJson(response, TransactionResponse::class.java)
                            val isCompleted =
                                transactionResponse?.data?.status?.lowercase() == "completed"
                            if (transactionResponse?.status != null && !isCompleted) {
                                onResult(
                                    ALATPayConstants.AlatPayTransactionStatus.SUCCESS,
                                    response,
                                    "Transaction Successful"
                                )
                            } else {
                                onResult(
                                    ALATPayConstants.AlatPayTransactionStatus.FAILED,
                                    response,
                                    "Transaction Failed"
                                )
                            }
                        }

                        @JavascriptInterface
                        fun onClose() {
                            onResult(
                                ALATPayConstants.AlatPayTransactionStatus.ABORTED,
                                null,
                                "Payment Gateway Closed"
                            )
                        }
                    }, "AndroidBridge")

                    // Embed the HTML directly
                    val htmlContent = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
        <title>Alatpay Payment</title>
    </head>
    <body>
        <script src="${checkoutData.environment.getEnvironment()}"></script>
        <script>
            let popup = Alatpay.setup({
                apiKey: "${checkoutData.key}",
                businessId: "${checkoutData.businessId}",
                email: "${checkoutData.customerEmail}",
                phone: "${checkoutData.customerPhone}",
                firstName: "${checkoutData.customerFirstName}",
                lastName: "${checkoutData.customerLastName}",
                metaData: "${checkoutData.reference}",
                currency: "${checkoutData.currencyCode.ifEmpty { ALATPayConstants.Currency.NGN.currencyName }}",
                amount: ${checkoutData.amount},
                ${if (checkoutData.themeColor.isNotEmpty()) "color: \"${checkoutData.themeColor}\"," else ""}
                onTransaction: function (response) {
                    console.log("API response is ", response);
                    window.AndroidBridge.onTransaction(JSON.stringify(response));
                },
                onClose: function () {
                    console.log("Payment gateway is closed");
                    window.AndroidBridge.onClose();
                }
            });

            popup.show();
        </script>
    </body>
    </html>
""".trimIndent()



                    loadDataWithBaseURL(
                        checkoutData.environment.getBaseUrl(),
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )

                    onSetWebView(this)
                }
            },
            update = {
                if (errorGot) {
                    onReload()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )

        if (newWindowResult != null) {
            NewWindow(
                resultMsg = newWindowResult,
                onDismiss = { newWindowResult = null }
            )
        }
    }


}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewWindow(
    resultMsg: Message?,
    onDismiss: () -> Unit
) {
    val newWebView = WebView(LocalContext.current).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = true // Required if the script needs local resources
        settings.allowContentAccess = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true
        }
        settings.setSupportMultipleWindows(true)
        settings.javaScriptCanOpenWindowsAutomatically = true
//        settings.userAgentString = System.getProperty("http.agent")
        WebView.setWebContentsDebuggingEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        settings.allowFileAccess = false

//        settings.userAgentString = System.getProperty("http.agent")
    }

    newWebView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            // Optional: do something when the new page is finished loading
        }
    }

    newWebView.webChromeClient = object : WebChromeClient() {
        override fun onCloseWindow(window: WebView?) {
            window?.destroy()
            onDismiss()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(id = R.string.cancel),
                tint = Color.Black
            )
        }

        AndroidView(
            factory = { newWebView },
            update = {
                resultMsg?.let { hmm ->
                    val transport = hmm.obj as WebViewTransport
                    transport.webView = it
                    resultMsg.sendToTarget()
                }
            }
        )
    }
}

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
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import com.alatpay_checkout_android.utils.constants.ALATPayConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.alatpay_checkout_android.BuildConfig
import com.alatpay_checkout_android.R
import com.alatpay_checkout_android.data.models.ALATPayCheckoutParcel
import com.alatpay_checkout_android.utils.getEnvironment
import com.alatpay_checkout_android.utils.isBrowsableUrl
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

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
    val result = (density.density * scaleFactor * 100).toInt()

    Box(
        modifier = modifier.fillMaxSize().semantics { contentDescription = contentDesc }
    ) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

//                    setInitialScale(result)
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.userAgentString =
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

                    // Referrer Policy
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        settings.safeBrowsingEnabled = true
                    }

                    // Enable debugging for testing headers and CORS
                    WebView.setWebContentsDebuggingEnabled(true)
                    // Set WebChromeClient for handling JavaScript dialogs and logs
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d("WebViewConsole", consoleMessage?.message() ?: "Unknown message")
                            return super.onConsoleMessage(consoleMessage)
                        }
                    }


//                    webChromeClient = object : WebChromeClient() {
//                        override fun onCreateWindow(
//                            view: WebView?,
//                            isDialog: Boolean,
//                            isUserGesture: Boolean,
//                            resultMsg: Message?
//                        ): Boolean {
//                            newWindowResult = resultMsg
//                            return true
//                        }
//
//                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//                            Log.d("WebViewConsole", consoleMessage?.message() ?: "No message")
//                            return true
//                        }
//
//                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val customHeaders = mapOf(
                                "accept" to "application/json",
                                "accept-encoding" to "gzip, deflate, br, zstd",
                                "accept-language" to "en-GB,en-ZA;q=0.9,en-US;q=0.8,en;q=0.7",
                                "cache-control" to "force-cache",
                                "connection" to "keep-alive",
                                "content-type" to "application/json",
//                                "origin" to BuildConfig.BASE_URL,
//                                "referer" to BuildConfig.BASE_URL,
                                "sec-ch-ua" to "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"",
                                "sec-ch-ua-mobile" to "?0",
//                                "sec-ch-ua-platform" to "\"Windows\"",
                                "sec-fetch-dest" to "empty",
                                "sec-fetch-mode" to "cors",
                                "sec-fetch-site" to "cross-site"
                            )

                            // Intercept the request and add custom headers
                            val newRequest = request?.let {
                                val newUrl = it.url.toString()
                                val newHeaders = HashMap(request.requestHeaders) // Copy original headers
                                newHeaders.putAll(customHeaders) // Add the custom headers

                                // Use WebResourceRequest to modify the request headers
                                object : WebResourceRequest {
                                    override fun getUrl(): Uri = Uri.parse(newUrl)
                                    override fun isForMainFrame(): Boolean = it.isForMainFrame

                                    @RequiresApi(Build.VERSION_CODES.N)
                                    override fun isRedirect(): Boolean = it.isRedirect

                                    override fun hasGesture(): Boolean = it.hasGesture()

                                    override fun getMethod(): String = it.method
                                    override fun getRequestHeaders(): Map<String, String> = newHeaders

                                }
                            }

                            // Perform the request with custom headers
                            return super.shouldInterceptRequest(view, newRequest)
                        }
                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                            errorGot = false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            onHasPageFinished(true)
                            onHasPageLoaded(true)
                        }

//                        override fun shouldInterceptRequest(
//                            view: WebView?,
//                            request: WebResourceRequest?
//                        ): WebResourceResponse? {
//                            val url = request?.url.toString()
//                            if (isBrowsableUrl(url)) {
//                                try {
//                                    // Open connection to the target URL
//                                    val connection = URL(url).openConnection() as HttpURLConnection
//                                    connection.setRequestProperty("Access-Control-Allow-Origin", "*")
//                                    connection.setRequestProperty("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE")
//                                    connection.setRequestProperty("Access-Control-Allow-Headers", "Content-Type, Authorization")
//
//                                    // Add subscription key or other headers if needed
////                                    connection.setRequestProperty("Subscription-Key", "4401dcd8e06c4c768d2e30619767e86b")
//
//                                    // Read the response from the server
//                                    val inputStream = connection.inputStream
//                                    val contentType = connection.contentType ?: "application/json"
//                                    val encoding = connection.contentEncoding ?: "UTF-8"
//
//                                    return WebResourceResponse(contentType, encoding, inputStream)
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }
//                            return super.shouldInterceptRequest(view, request)
//                        }

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
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            errorGot = error?.errorCode == -2
                            super.onReceivedError(view, request, error)

                            // Check if this is for the main frame or a subresource
                            if (request?.isForMainFrame == true) {
                                val errorDescription =
                                    error?.description?.toString() ?: "Unknown error"
                                Log.e("WebViewError", "Error: $errorDescription")
                                // Handle the error for the main frame (e.g., show an error page or message)
                            }
                        }
                    }

                    // Add JavaScript interface for callbacks
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onTransaction(response: String) {
                            Log.d("WebViewResponse", "Payload: $response")
                            onResult(
                                ALATPayConstants.AlatPayTransactionStatus.SUCCESS,
                                response,
                                "Transaction Successful"
                            )
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
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <meta name="referrer" content="strict-origin-when-cross-origin"> <!-- Referrer Policy set to strict-origin-when-cross-origin -->
                            <title>ALATPay Checkout</title>
                            <script src="${checkoutData.environment.getEnvironment()}"></script>
                        </head>
                        <body>
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
                                    onTransaction: function (response) {
                                        console.log("API response is ", response);
                                        window.AndroidBridge.onTransaction(JSON.stringify(response));
                                    },
                                    onClose: function () {
                                        console.log("Payment gateway is closed");
                                        window.AndroidBridge.onClose();
                                    }
                                });

                                console.log("Popup setup completed");
                                // Automatically show the payment popup
                                popup.show();
                                console.log("Popup.show() called");
                            </script>
                        </body>
                        </html>
                    """.trimIndent()

//                    loadData(
//                        htmlContent,
//                        "text/html",
//                        "UTF-8",
//                    )

                    loadDataWithBaseURL(
                        BuildConfig.BASE_URL,
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
            modifier = Modifier.fillMaxSize()
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

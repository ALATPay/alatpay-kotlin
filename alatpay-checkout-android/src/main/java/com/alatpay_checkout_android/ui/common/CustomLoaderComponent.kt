package com.alatpay_checkout_android.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alatpay_checkout_android.R
import com.alatpay_checkout_android.ui.theme.AlatPayCheckoutTheme

@Composable
fun AnimatedLoader() {
    val transition = rememberInfiniteTransition()

    val size by transition.animateFloat(
        initialValue = 20f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val loadingText = stringResource(R.string.loading)

    AlatPayCheckoutTheme {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(size.dp)
                    .padding(8.dp)
                    .animateContentSize()
                    .semantics { contentDescription = loadingText },
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

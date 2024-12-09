package com.alatpay_checkout_android.ui.common

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alatpay_checkout_android.R
import com.alatpay_checkout_android.ui.theme.AlatPayCheckoutTheme
import com.alatpay_checkout_android.ui.theme.white
import com.alatpay_checkout_android.ui.theme.wine

@Composable
fun DiagonalDebugLabel(
    modifier: Modifier = Modifier
) {
    AlatPayCheckoutTheme {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth(1f)) {
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Box(
                modifier = modifier
                    .rotate(45f)
                    .offset(20.dp, (-15).dp)
                    .background(wine)
                    .padding(horizontal = 28.dp)

            ) {
                Text(
                    text = stringResource(id = R.string.test_mode),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun NetworkBadge(modifier: Modifier = Modifier, showNetworkBadge: Boolean) {
    AnimatedVisibility(
        visible = showNetworkBadge,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()

    ) {
        Surface(
            modifier = modifier,
            color = Color.Red,
            shadowElevation = 4.dp
        ) {
            MaterialTheme {
                Text(
                    text = stringResource(id = R.string.slow_net),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp).semantics { contentDescription = "error" }
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    title: String = "Connection Failed!",
    msg: String,
    onTryAgainClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    AlatPayCheckoutTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme .colorScheme.background
                )
                .semantics { contentDescription = "error page" }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularBackgroundWithSVG(
                    svgId = R.drawable.nointernet_ic,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = msg,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(10.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .semantics { contentDescription = "try" },
                        bgColor = MaterialTheme.colorScheme.primary,
                        buttonTitle = stringResource(id = R.string.try_again),
                        buttonTitleColor = white,
                        onClick = onTryAgainClicked
                    )
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(10.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .semantics { contentDescription = "cancel" },
                        bgColor = MaterialTheme.colorScheme.background,
                        buttonTitle = stringResource(id = R.string.go_back),
                        buttonTitleColor = MaterialTheme.colorScheme.primary,
                        onClick = onCancelClicked
                    )
                }
            }
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    bgColor: Color,
    buttonTitle: String,
    buttonTitleColor: Color,
    onClick: () -> Unit
) {
    val  isDarkTheme: Boolean = isSystemInDarkTheme()
    val colorScheme = if (isDarkTheme) {
        lightColorScheme(
            primary = bgColor,
            primaryContainer = bgColor,
            secondary = bgColor,
            secondaryContainer = bgColor,
            background = bgColor,
            surface = bgColor,
            error = bgColor,
            onPrimary = bgColor,
            onSecondary = bgColor,
            onBackground = bgColor,
            onSurface = bgColor,
            onError = bgColor
        )
    } else {
        lightColorScheme(
            primary = bgColor,
            primaryContainer = bgColor,
            secondary = bgColor,
            secondaryContainer = bgColor,
            background = bgColor,
            surface = bgColor,
            error = bgColor,
            onPrimary = bgColor,
            onSecondary = bgColor,
            onBackground = bgColor,
            onSurface = bgColor,
            onError = bgColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Button(
            modifier = modifier,
            onClick = onClick
        ) {
            Text(text = buttonTitle, color = buttonTitleColor, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun CircularBackgroundWithSVG(@DrawableRes svgId: Int, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .background(color, CircleShape)
    ) {
        Image(
            painter = painterResource(id = svgId),
            contentDescription = stringResource(id = R.string.no_internet),
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
fun CustomAlertDialog(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onCancelClicked: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    if (isVisible) {
        AlatPayCheckoutTheme {
            BackHandler(onBack = onCancelClicked)
            AlertDialog(
                modifier = modifier.semantics { contentDescription = "alert dialog" },
                containerColor = MaterialTheme.colorScheme.background,
                title = {
                    Text(
                        stringResource(id = R.string.abort_payment_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                onDismissRequest = onCancelClicked,
                confirmButton = {
                    CustomButton(
                        modifier = Modifier.padding(horizontal = 10.dp).semantics { contentDescription = "ok" },
                        bgColor = MaterialTheme.colorScheme.background,
                        buttonTitle = stringResource(id = R.string.ok),
                        buttonTitleColor = MaterialTheme.colorScheme.primary,
                        onClick = onConfirmClicked
                    )
                },
                dismissButton = {
                    CustomButton(
                        modifier = Modifier.padding(horizontal = 10.dp).semantics { contentDescription = "cancel dialog" },
                        bgColor = MaterialTheme.colorScheme.background,
                        buttonTitle = stringResource(id = R.string.cancel),
                        buttonTitleColor = MaterialTheme.colorScheme.primary,
                        onClick = onCancelClicked
                    )
                }

            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreview() {
    AlatPayCheckoutTheme {
        ErrorScreen(
            msg = "Error Message",
            onTryAgainClicked = {},
            onCancelClicked = {}
        )
    }
}

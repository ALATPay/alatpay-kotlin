package com.alatpay_checkout_android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColorScheme(
    primary = blue,
    primaryContainer = Purple700,
    secondary = grey,
    onPrimary = lightGrey,
    onSecondary = neutralGrey,
    background = black,
    onBackground = white

)

private val LightColorPalette = lightColorScheme(
    primary = blue,
    primaryContainer = Purple700,
    secondary = grey,
    onPrimary = lightGrey,
    onSecondary = neutralGrey,
    background = white,
    onBackground = black
    /* Other default colors to override

    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AlatPayCheckoutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography =  MaterialTheme.typography,
        shapes = Shapes,
        content = content
    )
}

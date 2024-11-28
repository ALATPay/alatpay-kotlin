package com.alat.pay.android.sdk.sample.ui.theme

import java.util.UUID

internal fun generateRandomString(length: Int): String {
    val uuid = UUID.randomUUID().toString().replace("-", "")
    val timestamp = System.currentTimeMillis().toString()
    return uuid + timestamp
}
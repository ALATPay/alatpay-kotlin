package com.wemapos.samplesoftpos

data class TrackData(
    val track1Data: String,
    val track2Data: String,
    val track3Data: String,
    val cardNo: String,
    val expiryDate: String,
    val serviceCode: String,
    val isIccCard: Boolean,
    val ksn: String,
    val cardholderName: String,
    val tk1ValidResult: Int,
    val tk2ValidResult: Int,
    val tk3ValidResult: Int
)
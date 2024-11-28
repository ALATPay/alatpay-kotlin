package com.wemapos.samplesoftpos

import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.util.Log
import java.io.IOException


fun handleIsoDepCard(isoDep: IsoDep): TrackData? {
    try {
        // APDU command to read Track 1 data (example APDU, you may need to modify based on your card type)
        val readTrack1Apdu = byteArrayOf(
            0x80.toByte(), 0xB2.toByte(), 0x01.toByte(), 0x0C.toByte(), 0x00.toByte()
        )
        val track1Response = isoDep.transceive(readTrack1Apdu)
        val track1Data = parseTrackData(track1Response)

        // APDU command to read Track 2 data
        val readTrack2Apdu = byteArrayOf(
            0x80.toByte(), 0xB2.toByte(), 0x02.toByte(), 0x0C.toByte(), 0x00.toByte()
        )
        val track2Response = isoDep.transceive(readTrack2Apdu)
        val track2Data = parseTrackData(track2Response)

        // APDU command to read Track 3 data (if available)
        val readTrack3Apdu = byteArrayOf(
            0x80.toByte(), 0xB2.toByte(), 0x03.toByte(), 0x0C.toByte(), 0x00.toByte()
        )
        val track3Response = isoDep.transceive(readTrack3Apdu)
        val track3Data = parseTrackData(track3Response)

        // APDU command to read KSN (Key Serial Number)
        val readKSNApdu = byteArrayOf(
            0x00.toByte(), 0xCA.toByte(), 0x9F.toByte(), 0x17.toByte(), 0x00.toByte() // Example command for KSN, adjust as needed
        )
        val ksnResponse = isoDep.transceive(readKSNApdu)
        val ksn = parseKSN(ksnResponse)

        // Extract other card information
        val cardNo = extractCardNumberFromTrack(track2Data)
        val expiryDate = extractExpiryDateFromTrack(track2Data)
        val serviceCode = extractServiceCodeFromTrack(track2Data)
        val cardholderName = extractCardholderNameFromTrack(track1Data)

        return TrackData(
            track1Data = track1Data,
            track2Data = track2Data,
            track3Data = track3Data,
            cardNo = cardNo,
            expiryDate = expiryDate,
            serviceCode = serviceCode,
            isIccCard = true,  // Assuming this is an ICC card
            ksn = ksn,  // Dynamically loaded KSN
            cardholderName = cardholderName,
            tk1ValidResult = validateTrackData(track1Data),
            tk2ValidResult = validateTrackData(track2Data),
            tk3ValidResult = validateTrackData(track3Data)
        )
    }
    catch ( e: SecurityException) {
        // Handle the error, e.g., show a message to the user
        Log.d("NFC-IsoDep", "Exception: ${e.stackTrace}")
       return null
    }
    finally {
        isoDep.close()
    }
}


private fun extractCardNumberFromTrack(track2Data: String): String {
    // Parse Track 2 Data (card number is before '=' character)
    return track2Data.split("3D")[0]  // '3D' is '=' in hexadecimal
}

private fun extractExpiryDateFromTrack(track2Data: String): String {
    // Parse expiry date from Track 2 Data (after '=' character, first four digits)
    return track2Data.split("3D")[1].substring(0, 4) // Example format YYMM
}

private fun extractServiceCodeFromTrack(track2Data: String): String {
    // Extract service code from Track 2 Data (after expiry date, next 3 digits)
    return track2Data.split("3D")[1].substring(4, 7)
}

private fun extractCardholderNameFromTrack(track1Data: String): String {
    // Parse Track 1 Data to extract cardholder name (between ^ characters in hex format)
    val components = track1Data.split("5E") // '5E' is '^' in hexadecimal
    return if (components.size > 1) components[1] else "Unknown"
}

/**
 * Parses the byte array response from the NFC card and converts it to a string
 */
private fun parseTrackData(response: ByteArray): String {
    return response.joinToString("") { String.format("%02X", it) }
}
/**
 * Parses the byte array response for KSN and converts it to a readable string
 */
private fun parseKSN(response: ByteArray): String {
    return response.joinToString("") { String.format("%02X", it) }
}
fun handleNfcACard(nfcA: NfcA): TrackData? {
    try {
        // Close any previously connected technology
//        if (nfcA.isConnected) {
//            nfcA.close()
//        }
        // Connect to NFC-A card
        nfcA.connect() // Ensure the connection before transceive

        // Prepare APDU command to send to the card
        val selectApdu = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00) // Example APDU command

        // Retry logic for transceive operation
        val response = retryTransceive(nfcA, selectApdu)

        // Parse and extract payment information from the response
        val track1Data = extractTrack1Data(response)
        val track2Data = extractTrack2Data(response)
        val track3Data = extractTrack3Data(response)
        val cardNo = extractCardNumber(track2Data)
        val expiryDate = extractExpiryDate(track2Data)
        val serviceCode = extractServiceCode(track2Data)
        val cardholderName = extractCardholderName(track1Data)
        val ksn = extractKSN(response)
        val isIccCard = checkIfIccCard(response)

        // Return the populated TrackData object
        return TrackData(
            track1Data = track1Data,
            track2Data = track2Data,
            track3Data = track3Data,
            cardNo = cardNo,
            expiryDate = expiryDate,
            serviceCode = serviceCode,
            isIccCard = isIccCard,
            ksn = ksn,
            cardholderName = cardholderName,
            tk1ValidResult = validateTrackData(track1Data),
            tk2ValidResult = validateTrackData(track2Data),
            tk3ValidResult = validateTrackData(track3Data)
        )
    }
    catch ( e: SecurityException) {
        // Handle the error, e.g., show a message to the user
        Log.d("NFC-A", "Exception: ${e.message}")
        return null
    }
    catch (e: TagLostException) {
        Log.e("NFC", "Tag was lost during NFC-A communication", e)
        return null // Return null or handle appropriately
    } catch (e: IOException) {
        Log.e("NFC", "Error communicating with NFC-A card", e)
        return null
    } finally {
        try {
            // Close any previously connected technology
            if (nfcA.isConnected) {
                nfcA.close()
            }
        } catch (e: IOException) {
            Log.e("NFC", "Error closing NFC-A tag connection", e)
        }
    }
}

private fun retryTransceive(nfcA: NfcA, command: ByteArray, retryCount: Int = 3,delay: Long = 1000): ByteArray {
    var attempts = 0
    var lastException: TagLostException? = null

    while (attempts < retryCount) {
        try {
//            if (!nfcA.isConnected) {
//                nfcA.connect()
//            }
            return nfcA.transceive(command)
        } catch (e: TagLostException) {
            attempts++
            lastException = e
            Log.e("NFC", "Tag lost, retrying... Attempt: $attempts", e)

            // Add a delay between retries to allow time for re-establishing connection
            Thread.sleep(delay)
        } catch (e: IOException) {
            Log.e("NFC", "IOException during NFC communication", e)
            break
        }
    }

    // If retries are exhausted, throw the last exception or return null
    throw lastException ?: IOException("Unknown error during transceive")
}


fun handleNfcBCard(nfcB: NfcB): TrackData? {
    try {
        // Close any previously connected technology
//        if (nfcB.isConnected) {
//            nfcB.close()
//        }
        // Connect to NFC-B card
//        nfcB.connect() // Ensure the connection before transceive

        // Prepare APDU command to send to the card
        val selectApdu = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00) // Example APDU command

        // Retry logic for transceive operation
        val response = retryTransceiveNfcB(nfcB, selectApdu)

        // Extract payment information from the response
        val track1Data = extractTrack1Data(response)
        val track2Data = extractTrack2Data(response)
        val track3Data = extractTrack3Data(response)
        val cardNo = extractCardNumber(track2Data)
        val expiryDate = extractExpiryDate(track2Data)
        val serviceCode = extractServiceCode(track2Data)
        val cardholderName = extractCardholderName(track1Data)
        val ksn = extractKSN(response)
        val isIccCard = checkIfIccCard(response)

        // Return the populated TrackData object
        return TrackData(
            track1Data = track1Data,
            track2Data = track2Data,
            track3Data = track3Data,
            cardNo = cardNo,
            expiryDate = expiryDate,
            serviceCode = serviceCode,
            isIccCard = isIccCard,
            ksn = ksn,
            cardholderName = cardholderName,
            tk1ValidResult = validateTrackData(track1Data),
            tk2ValidResult = validateTrackData(track2Data),
            tk3ValidResult = validateTrackData(track3Data)
        )
    }
    catch ( e: SecurityException) {
        // Handle the error, e.g., show a message to the user
        Log.d("NFC-B", "Exception: ${e.message}")
        return null
    }
    catch (e: TagLostException) {
        Log.e("NFC", "Tag was lost during NFC-B communication", e)
        return null // Handle tag loss gracefully
    } catch (e: IOException) {
        Log.e("NFC", "Error communicating with NFC-B card", e)
        return null
    } finally {
        try {
            // Close any previously connected technology
            if (nfcB.isConnected) {
                nfcB.close()
            }
        } catch (e: IOException) {
            Log.e("NFC", "Error closing NFC-B tag connection", e)
        }
    }
}
private fun retryTransceiveNfcB(nfcB: NfcB, command: ByteArray, retryCount: Int = 3,delay: Long = 1000): ByteArray {
    var attempts = 0
    var lastException: TagLostException? = null

    while (attempts < retryCount) {
        try {
            if (!nfcB.isConnected) {
                nfcB.connect()
            }
            return nfcB.transceive(command)
        } catch (e: TagLostException) {
            attempts++
            lastException = e
            Log.e("NFC", "Tag lost, retrying... Attempt: $attempts", e)

            // Add a delay between retries to allow time for re-establishing connection
            Thread.sleep(delay)
        } catch (e: IOException) {
            Log.e("NFC", "IOException during NFC communication", e)
            break
        }
    }

    // If retries are exhausted, throw the last exception or return null
    throw lastException ?: IOException("Unknown error during transceive")
}

fun handleFelicaCard(nfcF: NfcF): TrackData? {
    try {
        // Close any previously connected technology
//        if (nfcF.isConnected) {
//            nfcF.close()
//        }
        // Connect to the FeliCa card
//        nfcF.connect()

        // Example command to request system code from a FeliCa card
        // FeliCa cards typically use specific commands, like 0x0B for requesting system code
        val requestServiceCode = byteArrayOf(0x00, 0x0B)

        // Retry logic for transceive operation
        val response = retryTransceiveFelica(nfcF, requestServiceCode)

        // Extract payment information from the FeliCa response
        val track1Data = extractTrack1Data(response)
        val track2Data = extractTrack2Data(response)
        val track3Data = extractTrack3Data(response)
        val cardNo = extractCardNumber(track2Data)
        val expiryDate = extractExpiryDate(track2Data)
        val serviceCode = extractServiceCode(track2Data)
        val cardholderName = extractCardholderName(track1Data)
        val ksn = extractKSN(response)
        val isIccCard = checkIfIccCard(response)

        // Return the populated TrackData object
        return TrackData(
            track1Data = track1Data,
            track2Data = track2Data,
            track3Data = track3Data,
            cardNo = cardNo,
            expiryDate = expiryDate,
            serviceCode = serviceCode,
            isIccCard = isIccCard,
            ksn = ksn,
            cardholderName = cardholderName,
            tk1ValidResult = validateTrackData(track1Data),
            tk2ValidResult = validateTrackData(track2Data),
            tk3ValidResult = validateTrackData(track3Data)
        )
    }
    catch ( e: SecurityException) {
        // Handle the error, e.g., show a message to the user
        Log.d("NFC-F", "Exception: ${e.message}")
        return null
    }
    catch (e: TagLostException) {
        Log.e("NFC", "Tag was lost during FeliCa communication", e)
        return null
    } catch (e: IOException) {
        Log.e("NFC", "Error communicating with FeliCa card", e)
        return null
    } finally {
        try {
            if (nfcF.isConnected) {
                nfcF.close()
            }
        } catch (e: IOException) {
            Log.e("NFC", "Error closing FeliCa tag connection", e)
        }
    }
}
private fun retryTransceiveFelica(nfcF: NfcF, command: ByteArray, retryCount: Int = 3,delay: Long = 1000): ByteArray {
    var attempts = 0
    var lastException: TagLostException? = null

    while (attempts < retryCount) {
        try {
            return nfcF.transceive(command)
        } catch (e: TagLostException) {
            attempts++
            lastException = e
            Log.e("NFC", "Tag lost, retrying... Attempt: $attempts", e)

            // Add a slight delay between retries
            Thread.sleep(500)
        }
    }

    throw lastException ?: IOException("Unknown error during FeliCa transceive")
}

fun handleNfcVCard(nfcV: NfcV): TrackData? {
    try {
//        if (nfcV.isConnected) {
//            nfcV.close()
//        }
        // Ensure the NFC-V tag is connected
//        nfcV.connect()

        // Example read command for NFC-V (ISO/IEC 15693)
        // 0x20 is a flag, 0x00 is the Read Single Block command
        val readCommand = byteArrayOf(0x20, 0x20, 0x00)
        val response = retryTransceiveNfcV(nfcV, readCommand)

        // Extract payment information from the NFC-V response
        val track1Data = extractTrack1Data(response)
        val track2Data = extractTrack2Data(response)
        val track3Data = extractTrack3Data(response)
        val cardNo = extractCardNumber(track2Data)
        val expiryDate = extractExpiryDate(track2Data)
        val serviceCode = extractServiceCode(track2Data)
        val cardholderName = extractCardholderName(track1Data)
        val ksn = extractKSN(response)
        val isIccCard = checkIfIccCard(response)

        // Return the populated TrackData object
        return TrackData(
            track1Data = track1Data,
            track2Data = track2Data,
            track3Data = track3Data,
            cardNo = cardNo,
            expiryDate = expiryDate,
            serviceCode = serviceCode,
            isIccCard = isIccCard,
            ksn = ksn,
            cardholderName = cardholderName,
            tk1ValidResult = validateTrackData(track1Data),
            tk2ValidResult = validateTrackData(track2Data),
            tk3ValidResult = validateTrackData(track3Data)
        )
    }
    catch ( e: SecurityException) {
        // Handle the error, e.g., show a message to the user
        Log.d("NFC-V", "Exception: ${e.message}")
        return null
    }
    catch (e: TagLostException) {
        Log.e("NFC", "Tag was lost during NFC-V communication", e)
        return null
    } catch (e: IOException) {
        Log.e("NFC", "Error communicating with NFC-V card", e)
        return null
    } finally {
        try {
            if (nfcV.isConnected) {
                nfcV.close()
            }
        } catch (e: IOException) {
            Log.e("NFC", "Error closing NFC-V tag connection", e)
        }
    }
}
private fun retryTransceiveNfcV(nfcV: NfcV, command: ByteArray, retryCount: Int = 3,delay: Long = 1000): ByteArray {
    var attempts = 0
    var lastException: TagLostException? = null

    while (attempts < retryCount) {
        try {
            if (!nfcV.isConnected) {
                nfcV.connect()
            }
            return nfcV.transceive(command)
        } catch (e: TagLostException) {
            attempts++
            lastException = e
            Log.e("NFC", "Tag lost, retrying... Attempt: $attempts", e)

            // Add a delay between retries to allow time for re-establishing connection
            Thread.sleep(delay)
        } catch (e: IOException) {
            Log.e("NFC", "IOException during NFC communication", e)
            break
        }
    }

    // If retries are exhausted, throw the last exception or return null
    throw lastException ?: IOException("Unknown error during transceive")
}



private fun extractTrack1Data(response: ByteArray): String {
    // Parse and return Track1 data from the raw response (example logic)
    return response.slice(0..79).toByteArray().toString(Charsets.US_ASCII)
}
private fun extractTrack2Data(response: ByteArray): String {
    // Parse and return Track2 data from the raw response (example logic)
    return response.slice(80..159).toByteArray().toString(Charsets.US_ASCII)
}
private fun extractTrack3Data(response: ByteArray): String {
    // Parse and return Track3 data from the raw response (if available)
    return response.slice(160..239).toByteArray().toString(Charsets.US_ASCII)
}
private fun extractCardNumber(track2Data: String): String {
    // Example logic: Extract card number from Track2 data (first 16 digits)
    return track2Data.substring(0, 16)
}
private fun extractExpiryDate(track2Data: String): String {
    // Example logic: Expiry date is typically in YYMM format in Track2 data
    val expiryYear = "20" + track2Data.substring(17, 19)
    val expiryMonth = track2Data.substring(19, 21)
    return "$expiryMonth/$expiryYear"
}
private fun extractServiceCode(track2Data: String): String {
    // Example logic: Service code is typically found in positions 21 to 23 of Track2
    return track2Data.substring(21, 24)
}
private fun extractCardholderName(track1Data: String): String {
    // Cardholder name is usually present in Track1 data
    val nameStartIndex = track1Data.indexOf('^') + 1
    val nameEndIndex = track1Data.indexOf('^', nameStartIndex)
    return track1Data.substring(nameStartIndex, nameEndIndex).trim()
}
private fun extractKSN(response: ByteArray): String {
    // Example logic for KSN extraction
    return response.joinToString("") { String.format("%02X", it) }
}
private fun checkIfIccCard(response: ByteArray): Boolean {
    // Example logic to determine if the card is ICC based on response
    return response.contains(0x90.toByte()) // Check for ICC-specific marker
}
private fun validateTrackData(trackData: String): Int {
    // Example validation logic for track data (returning 1 for valid, 0 for invalid)
    return if (trackData.isNotEmpty()) 1 else 0
}



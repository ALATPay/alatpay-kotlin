package com.wemapos.samplesoftpos

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.wemapos.samplesoftpos.databinding.ActivityMainBinding
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
//        if (nfcAdapter != null && nfcAdapter.isEnabled) {
//            // Use ReaderMode to handle NFC detection
//            nfcAdapter.enableReaderMode(
//                this,
//                { tag ->
//                    runOnUiThread {
//                        // Pass the tag to handleNfcIntent for processing
//                        handleNfcIntent(Intent().apply {
//                            putExtra(NfcAdapter.EXTRA_TAG, tag)
//                        })
//                    }
//                },
//                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
//                        NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V ,
//                Bundle().apply {
//                    putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000)
//                }
//            )
//        }
        if (nfcAdapter != null &&  nfcAdapter.isEnabled) {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

            // Define intent filters for the types of NFC actions you want to listen for
            val intentFiltersArray = arrayOf<IntentFilter>()

            // Define which NFC technologies to listen for (NfcA, NfcB, IsoDep, etc.)
            val techListArray = arrayOf(
                arrayOf(NfcA::class.java.name, IsoDep::class.java.name),
                arrayOf(NfcB::class.java.name, IsoDep::class.java.name),
                arrayOf(NfcF::class.java.name),
                arrayOf(NfcV::class.java.name)
            )

            // Enable foreground dispatch to make sure your activity is prioritized for NFC actions
            nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListArray)
        }
    }

    override fun onPause() {
        super.onPause()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.disableForegroundDispatch(this)
//        nfcAdapter?.disableReaderMode(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        Log.d("NFC", "Intent received: ${intent.toString()}")

        val action = intent.action
        Log.d("NFC", "Action: $action")

//        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
//        Log.d("NFC", "Tag: $tag")
        nfcAdapter?.enableReaderMode(
            this,
            { tag ->
                runOnUiThread {
//                    handleNfcIntent(intent)// Handle the tag (NfcA, NfcB, IsoDep, etc.)
                    handleTag(tag) // Handle the tag (NfcA, NfcB, IsoDep, etc.)
                }
            },
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V,
            Bundle().apply {
                putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 2000) // Increase delay before checking presence
            }
        )

    }
    private fun handleTag(tag: Tag?){
        tag?.let {
            // Determine the technologies supported by the tag
            val nfcTechList = it.techList
            Log.d("NFC", "Tech list: ${nfcTechList.joinToString()}")
            when {
                nfcTechList.contains(NfcA::class.java.name) -> {
                    val nfcA = NfcA.get(tag)
                    nfcA?.let { nfcAData ->
                        try {
//                            if (!nfcAData.isConnected) {
//                                nfcAData.connect() // Only connect if not already connected
//                            }

                            // Process the NFC-A card
                            val trackData = handleNfcACard(nfcAData)
                            Log.d("NFC-A", "Track Data: $trackData")

                        }
                        catch (e: TagLostException) {
                            Log.e("NFC", "Tag lost during communication. Please keep the NFC tag near the device.", e)
                            // Optionally, show a Toast or Snackbar to inform the user
                            runOnUiThread {
                                Toast.makeText(this, "NFC tag lost. Please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: IOException) {
                            Log.e("NFC", "Error communicating with NFC-A card", e)
                        } finally {
                            try {
                                if (nfcAData.isConnected) {
                                    nfcAData.close()
                                }
                            } catch (e: IOException) {
                                Log.e("NFC", "Error closing NFC-A tag connection", e)
                            }
                        }
                    }
                }
                nfcTechList.contains(NfcB::class.java.name) -> {
                    val nfcB = NfcB.get(tag)
                    nfcB?.let { nfcBData ->
                        try {
                            if (!nfcBData.isConnected) {
                                nfcBData.connect() // Only connect if not already connected
                            }

                            // Process the NFC-B card
                            val trackData = handleNfcBCard(nfcBData)
                            Log.d("NFC-B", "Track Data: $trackData")
                        }
                        catch (e: TagLostException) {
                            Log.e("NFC", "Tag lost during communication. Please keep the NFC tag near the device.", e)
                            // Optionally, show a Toast or Snackbar to inform the user
                            runOnUiThread {
                                Toast.makeText(this, "NFC tag lost. Please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: IOException) {
                            Log.e("NFC", "Error communicating with NFC-B card", e)
                        } finally {
                            try {
                                if (nfcBData.isConnected) {
                                    nfcBData.close()
                                }
                            } catch (e: IOException) {
                                Log.e("NFC", "Error closing NFC-B tag connection", e)
                            }
                        }
                    }
                }
                nfcTechList.contains(NfcF::class.java.name) -> {
                    val nfcF = NfcF.get(tag)
                    nfcF?.let { nfcCData ->
                        try {
                            if (!nfcCData.isConnected) {
                                nfcCData.connect() // Only connect if not already connected
                            }

                            // Process the NFC-F card
                            val trackData = handleFelicaCard(nfcCData)
                            Log.d("NFC-F", "Track Data: $trackData")
                        }
                        catch (e: TagLostException) {
                            Log.e("NFC", "Tag lost during communication. Please keep the NFC tag near the device.", e)
                            // Optionally, show a Toast or Snackbar to inform the user
                            runOnUiThread {
                                Toast.makeText(this, "NFC tag lost. Please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: IOException) {
                            Log.e("NFC", "Error communicating with NFC-F card", e)
                        } finally {
                            try {
                                if (nfcCData.isConnected) {
                                    nfcCData.close()
                                }
                            } catch (e: IOException) {
                                Log.e("NFC", "Error closing NFC-F tag connection", e)
                            }
                        }
                    }
                }
                nfcTechList.contains(NfcV::class.java.name) -> {
                    val nfcV = NfcV.get(tag)
                    nfcV?.let { nfcVData ->
                        try {
                            if (!nfcVData.isConnected) {
                                nfcVData.connect() // Only connect if not already connected
                            }

                            // Process the NFC-V card
                            val trackData = handleNfcVCard(nfcVData)
                            Log.d("NFC-V", "Track Data: $trackData")
                        }
                        catch (e: TagLostException) {
                            Log.e("NFC", "Tag lost during communication. Please keep the NFC tag near the device.", e)
                            // Optionally, show a Toast or Snackbar to inform the user
                            runOnUiThread {
                                Toast.makeText(this, "NFC tag lost. Please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: IOException) {
                            Log.e("NFC", "Error communicating with NFC-V card", e)
                        } finally {
                            try {
                                if (nfcVData.isConnected) {
                                    nfcVData.close()
                                }
                            } catch (e: IOException) {
                                Log.e("NFC", "Error closing NFC-V tag connection", e)
                            }
                        }
                    }
                }
                nfcTechList.contains(IsoDep::class.java.name) ->{
                    val isoDep = IsoDep.get(tag)
//                    isoDep.timeout = 5000 // Set a timeout of 5 seconds
                    isoDep?.let { isoDepCard ->
                        try {
//                            if (!isoDepCard.isConnected) {
//                                isoDepCard.connect() // Only connect if not already connected
//                            }

                            // Process the NFC-IsoDep card
                            val trackData = handleIsoDepCard(isoDepCard)
                            Log.d("NFC-IsoDep", "Track Data: $trackData")
                        }
                        catch (e: TagLostException) {
                            Log.e("NFC", "Tag lost during communication. Please keep the NFC tag near the device.", e)
                            // Optionally, show a Toast or Snackbar to inform the user
                            runOnUiThread {
                                Toast.makeText(this, "NFC tag lost. Please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: IOException) {
                            Log.e("NFC", "Error communicating with NFC-IsoDep card", e)
                        } finally {
                            try {
                                if (isoDepCard.isConnected) {
                                    isoDepCard.close()
                                }
                            } catch (e: IOException) {
                                Log.e("NFC", "Error closing NFC-IsoDep tag connection", e)
                            }
                        }
                    }
                }
                else -> {
                    Log.d("NFC", "Unsupported NFC technology")
                }
            }

        }
    }
    private fun handleNfcIntent(intent: Intent?) {
        // Log the action to help debug
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val action = intent?.action
        Log.d("NFC", "Intent action: $action")

        // Handle case when the action is null
        if (action == null) {
            Log.e("NFC", "Action is null")
            return
        }
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let {
                // Determine the technologies supported by the tag
                val nfcTechList = it.techList
                Log.d("NFC", "Tech list: ${nfcTechList.joinToString()}")
                when {
                    nfcTechList.contains(NfcA::class.java.name) -> {
                        val nfcA = NfcA.get(tag)
                        nfcA?.let { nfcAData ->
                            try {
                                if (!nfcAData.isConnected) {
                                    nfcAData.connect() // Only connect if not already connected
                                }

                                // Process the NFC-A card
                                val trackData = handleNfcACard(nfcAData)
                                Log.d("NFC-A", "Track Data: $trackData")
                                nfcAData.close()
                            } catch (e: IOException) {
                                Log.e("NFC", "Error communicating with NFC-A card", e)
                            } finally {
                                try {
                                    if (nfcAData.isConnected) {
                                        nfcAData.close()
                                    }
                                } catch (e: IOException) {
                                    Log.e("NFC", "Error closing NFC-A tag connection", e)
                                }
                            }
                        }
                    }
                    nfcTechList.contains(NfcB::class.java.name) -> {
                        val nfcB = NfcB.get(tag)
                        nfcB?.let { nfcBData ->
                            try {
                                if (!nfcBData.isConnected) {
                                    nfcBData.connect() // Only connect if not already connected
                                }

                                // Process the NFC-B card
                                val trackData = handleNfcBCard(nfcBData)
                                Log.d("NFC-B", "Track Data: $trackData")
                                nfcBData.close()
                            } catch (e: IOException) {
                                Log.e("NFC", "Error communicating with NFC-B card", e)
                            } finally {
                                try {
                                    if (nfcBData.isConnected) {
                                        nfcBData.close()
                                    }
                                } catch (e: IOException) {
                                    Log.e("NFC", "Error closing NFC-B tag connection", e)
                                }
                            }
                        }
                    }
                    nfcTechList.contains(NfcF::class.java.name) -> {
                        val nfcF = NfcF.get(tag)
                        nfcF?.let { nfcCData ->
                            try {
                                if (!nfcCData.isConnected) {
                                    nfcCData.connect() // Only connect if not already connected
                                }

                                // Process the NFC-F card
                                val trackData = handleFelicaCard(nfcCData)
                                Log.d("NFC-F", "Track Data: $trackData")
                                nfcCData.close()
                            } catch (e: IOException) {
                                Log.e("NFC", "Error communicating with NFC-F card", e)
                            } finally {
                                try {
                                    if (nfcCData.isConnected) {
                                        nfcCData.close()
                                    }
                                } catch (e: IOException) {
                                    Log.e("NFC", "Error closing NFC-F tag connection", e)
                                }
                            }
                        }
                    }
                    nfcTechList.contains(NfcV::class.java.name) -> {
                        val nfcV = NfcV.get(tag)
                        nfcV?.let { nfcVData ->
                            try {
                                if (!nfcVData.isConnected) {
                                    nfcVData.connect() // Only connect if not already connected
                                }

                                // Process the NFC-V card
                                val trackData = handleNfcVCard(nfcVData)
                                Log.d("NFC-V", "Track Data: $trackData")
                                nfcVData.close()
                            } catch (e: IOException) {
                                Log.e("NFC", "Error communicating with NFC-V card", e)
                            } finally {
                                try {
                                    if (nfcVData.isConnected) {
                                        nfcVData.close()
                                    }
                                } catch (e: IOException) {
                                    Log.e("NFC", "Error closing NFC-V tag connection", e)
                                }
                            }
                        }
                    }
                    nfcTechList.contains(IsoDep::class.java.name) ->{
                        val isoDep = IsoDep.get(tag)
                        isoDep?.let { isoDepCard ->
                            try {
                                if (!isoDepCard.isConnected) {
                                    isoDepCard.connect() // Only connect if not already connected
                                }

                                // Process the NFC-IsoDep card
                                val trackData = handleIsoDepCard(isoDepCard)
                                Log.d("NFC-IsoDep", "Track Data: $trackData")
                                isoDepCard.close()
                            } catch (e: IOException) {
                                Log.e("NFC", "Error communicating with NFC-IsoDep card", e)
                            } finally {
                                try {
                                    if (isoDepCard.isConnected) {
                                        isoDepCard.close()
                                    }
                                } catch (e: IOException) {
                                    Log.e("NFC", "Error closing NFC-IsoDep tag connection", e)
                                }
                            }
                        }
                    }
                    else -> {
                        Log.d("NFC", "Unsupported NFC technology")
                    }
                }

            }
        }
        else {
            Log.e("NFC", "Unsupported NFC action")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
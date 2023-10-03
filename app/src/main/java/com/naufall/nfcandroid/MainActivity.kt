package com.naufall.nfcandroid

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import id.co.solusinegeri.katalisedc.ui.NFC.NfcUtils
import id.co.solusinegeri.katalisedc.ui.NFC.WritableTag

class MainActivity : AppCompatActivity() {
    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null
    var nfcid: String = ""
    val txtNfc: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNfcAdapter()
    }

    fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter
    }


    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }
add
    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val flag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, flag)
            adapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e(getTag(), "Error enabling NFC foreground dispatch", ex)
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e(getTag(), "Error disabling NFC foreground dispatch", ex)
        }
    }

    private fun getTag() = "MainActivity"

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e(getTag(), "Unsupported tag tapped", e)
            return
        }
        tagId = tag!!.tagId
        Log.d("nfc", tagId.toString())
        returnValue(tagId.toString())
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null) {
                onTagTapped(NfcUtils.getUID(intent), NfcUtils.getData(rawMsgs))
            }
        }
    }


    fun returnValue(str: String) : String{
        var buffer = ""

        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }
        buffer = buffer.trim()
        buffer = buffer.takeLast(8)

        nfcid = buffer.toLong(16).toString()
        Log.d("nfc", nfcid)
        txtNfc?.text = nfcid.toString()
        return  nfcid
    }

    private fun onTagTapped(superTagId: String, superTagData: String) {
//        txt_nfc.text = superTagId
//        ed_nfc.text = superTagData
    }

}
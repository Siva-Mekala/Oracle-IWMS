package com.plcoding.oraclewms

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.work.WorkInfo
import androidx.work.WorkManager

object Utils {
    private lateinit var context: Context;

    fun isWorkScheduled(context: Context, tag: String): Boolean {
        val instance = WorkManager.getInstance(context)
        val statuses = instance.getWorkInfosByTag(tag)
        val workInfos: List<WorkInfo>? = statuses.get()
        var running = false
        if (workInfos == null || workInfos.size == 0) return false
        for (workStatus in workInfos) {
            running =
                (workStatus.state == WorkInfo.State.RUNNING) or (workStatus.state == WorkInfo.State.ENQUEUED)
        }
        return running
    }

    fun isDeviceLocked(context: Context): Boolean {
        var isLocked = false
        // First we check the locked state
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val inKeyguardRestrictedInputMode = keyguardManager.isKeyguardLocked()

        isLocked = if (inKeyguardRestrictedInputMode) {
            true
        } else {
            // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
            // so we need to check if screen on for this case

            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                !powerManager.isInteractive
            } else {
                //noinspection deprecation
                !powerManager.isScreenOn
            }
        }

        //Log.d("Utils", String.format("Now device is %s.", if (isLocked) "locked" else "unlocked"))
        return isLocked
    }

    fun deviceUUID(): String {

        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun initSharedPref(lcontext: Context) {
        context = lcontext
    }


    fun getControlCharacterValueOptimized(controlChar: String): String {
        return when (controlChar) {
            "Ctrl-A" -> "\u0001"
            "Ctrl-B" -> "\u0002"
            "Ctrl-C" -> "\u0003"
            "Ctrl-D" -> "\u0004"
            "Ctrl-E" -> "\u0005"
            "Ctrl-F" -> "\u0006"
            "Ctrl-G" -> "\u0007"
            "Ctrl-H" -> "\u0008"
            "Ctrl-I" -> "\u0009"
            "Ctrl-J" -> "\u000A"
            "Ctrl-K" -> "\u000B"
            "Ctrl-L" -> "\u000C"
            "Ctrl-M" -> "\u000D"
            "Ctrl-N" -> "\u000E"
            "Ctrl-O" -> "\u000F"
            "Ctrl-P" -> "\u0010"
            "Ctrl-Q" -> "\u0011"
            "Ctrl-R" -> "\u0012"
            "Ctrl-S" -> "\u0013"
            "Ctrl-T" -> "\u0014"
            "Ctrl-U" -> "\u0015"
            "Ctrl-V" -> "\u0016"
            "Ctrl-W" -> "\u0017"
            "Ctrl-X" -> "\u0018"
            "Ctrl-Y" -> "\u0019"
            "Ctrl-Z" -> "\u001A"
            else -> "Invalid control character"
        }
    }


}



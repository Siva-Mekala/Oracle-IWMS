package com.plcoding.oraclewms

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.work.WorkInfo
import androidx.work.WorkManager

object Utils {
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
}
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

    val CTRL_A = "\u0001"//	Start of Heading (SOH)
    val CTRL_B	= "\u0002"//	Start of Text (STX)
    val CTRL_C =	"\u0003"//	End of Text (ETX)
    val CTRL_D	= "\u0004"//	End of Transmission (EOT)
    val CTRL_E	="\u0005"//	Enquiry (ENQ)
    val CTRL_F	="\u0006"//	Acknowledge (ACK)
    val CTRL_G	="\u0007"//	Bell (BEL)
    val CTRL_H	="\u0008"//	Backspace (BS)
    val CTRL_I=	"\u0009"//	Horizontal Tab (HT)
    val CTRL_J	="\u000A"//	Line Feed (LF)
    val CTRL_K=	"\u000B"//	Vertical Tab (VT)
    val CTRL_L=	"\u000C"//	Form Feed (FF)
    val CTRL_M=	"\u000D"//	Carriage Return (CR)
    val CTRL_N=	"\u000E"//	Shift Out (SO)
    val CTRL_O=	"\u000F"//	Shift In (SI)
    val CTRL_P=	"\u0010"//	Data Link Escape (DLE)
    val CTRL_Q=	"\u0011"//	Device Control 1 (DC1)
    val CTRL_R=	"\u0012"//	Device Control 2 (DC2)
    val CTRL_S=	"\u0013"//	Device Control 3 (DC3)
    val CTRL_T=	"\u0014"//	Device Control 4 (DC4)
    val CTRL_U=	"\u0015"//	Negative Acknowledge (NAK)
    val CTRL_V=	"\u0016"//	Synchronous Idle (SYN)
    val CTRL_W=	"\u0017"//	End of Transmission Block
    val CTRL_X	="\u0018"//	Cancel (CAN)
    val CTRL_Y=	"\u0019"//	End of Medium (EM)
    val CTRL_Z=	"\u001A"//	Substitute (SUB)
}
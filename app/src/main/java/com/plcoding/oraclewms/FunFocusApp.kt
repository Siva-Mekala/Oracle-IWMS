package com.plcoding.oraclewms

import android.app.Application

class FunFocusApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize SharedPreferences
        SharedPref.initSharedPref(this)
    }
}
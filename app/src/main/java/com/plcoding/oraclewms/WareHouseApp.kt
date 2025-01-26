package com.plcoding.oraclewms

import android.app.Application

class WareHouseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize SharedPreferences
        SharedPref.initSharedPref(this)
    }
}
package com.plcoding.oraclewms

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.plcoding.oraclewms.api.NetworkConnectivityObserver

class WareHouseApp : Application() {

    // Declare your LiveData variable here
    private val _userName = MutableLiveData("")
    val userName: LiveData<String> = _userName

    override fun onCreate() {
        super.onCreate()
        Utils.initSharedPref(this)
        SharedPref.initSharedPref(this)
        _userName.value = SharedPref.getUserName()
    }

    fun setName(name: String) {
        _userName.postValue(name)
    }
}
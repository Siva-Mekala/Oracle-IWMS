package com.plcoding.oraclewms

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPref {
    private const val SHARED_FILE_NAME = "fun_focus_shared_pref"
    private const val KEY_AUTH_TOKEN = "AUTH_TOKEN"
    private const val KEY_IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
    private const val KEY_NFT_TYPE = "NFT_TYPE"
    private const val KEY_IS_SCREEN_LOCKED = "SCREEN_LOCKED"

    private lateinit var sharedPref: SharedPreferences
    private lateinit var context: Context

    fun initSharedPref(lcontext: Context) {
        context = lcontext
        sharedPref = context.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun setToken(isEnable: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_AUTH_TOKEN, isEnable)
        editor.commit()
    }

    fun getToken(): String? {
        return sharedPref.getString(KEY_AUTH_TOKEN, null)
    }

    fun setType(type: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NFT_TYPE, type)
        editor.commit()
    }

    fun getType(): String? {
        return sharedPref.getString(KEY_NFT_TYPE, null)
    }

    fun setUserLoggedIn(state: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_USER_LOGGED_IN, state)
        editor.commit()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_USER_LOGGED_IN, false)
    }

    fun setScreenLocked(state: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_SCREEN_LOCKED, state)
        editor.commit()
    }

    fun isScreenLocked(): Boolean {
        return sharedPref.getBoolean(KEY_IS_SCREEN_LOCKED, false)
    }

    fun deleteAllPref() {
        sharedPref.edit {
            remove(KEY_AUTH_TOKEN)
            apply()
        }

    }


}


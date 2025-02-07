package com.plcoding.oraclewms

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPref {
    private const val SHARED_FILE_NAME = "fun_focus_shared_pref"
    private const val KEY_AUTH_TOKEN = "AUTH_TOKEN"
    private const val KEY_IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN"
    private const val KEY_ENV = "ENV"
    private const val KEY_RESPONSE = "RESPONSE"
    private const val ENV_RESPONSE = "ENV_RESPONSE"
    private const val KEY_FIRSTTIME_TC = "KEY_FIRSTTIME_TC"
    private const val KEY_HOME_INFO = "KEY_HOME_INFO"
    private const val KEY_SCREEN_NAME = "KEY_SCREEN_NAME"
    private const val KEY_USER_NAME = "KEY_USER_NAME"
    private const val KEY_DATE_FORMATE = "KEY_DATE_FORMATE"

    private lateinit var sharedPref: SharedPreferences
    private lateinit var context: Context

    fun setScreenName(screenName: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_SCREEN_NAME, screenName)
        editor.commit()
    }

    fun getScreenName(): String? {
        return sharedPref.getString(KEY_SCREEN_NAME, null)
    }

    fun setEnvResponse(env: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(ENV_RESPONSE, env)
        editor.commit()

    }

    fun getEnvResponse(): String? {
        return sharedPref.getString(ENV_RESPONSE, null)
    }

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

    fun setEnv(env: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_ENV, env)
        editor.commit()
    }

    fun getEnv(): String? {
        return sharedPref.getString(KEY_ENV, null)
    }

    fun setResponse(response: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_RESPONSE, response)
        editor.commit()
    }

    fun getResponse(): String? {
        return sharedPref.getString(KEY_RESPONSE, null)
    }

    fun setUserName(name: String) {
        val app = context.applicationContext as WareHouseApp
        app.setName(name)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_USER_NAME, name)
        editor.commit()
    }

    fun getUserName(): String? {
        return sharedPref.getString(KEY_USER_NAME, null)
    }

    fun setDateFormat(format: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_DATE_FORMATE, format)
        editor.commit()
    }

    fun getDateFormat(): String? {
        return sharedPref.getString(KEY_DATE_FORMATE, null)
    }

    fun setHomeInfo(info: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_HOME_INFO, info)
        editor.commit()
    }

    fun getHomeInfo(): String? {
        return sharedPref.getString(KEY_HOME_INFO, null)
    }

    fun setUserLoggedIn(state: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_USER_LOGGED_IN, state)
        editor.commit()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_USER_LOGGED_IN, false)
    }

    fun setSeenTc(state: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_FIRSTTIME_TC, state)
        editor.commit()
    }

    fun isSeenTc(): Boolean {
        return sharedPref.getBoolean(KEY_FIRSTTIME_TC, false)
    }

    fun deleteAllPref() {
        sharedPref.edit {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_IS_USER_LOGGED_IN)
            remove(KEY_ENV)
            remove(KEY_RESPONSE)
            remove(KEY_HOME_INFO)
            remove(KEY_SCREEN_NAME)
            remove(KEY_USER_NAME)
            remove(KEY_DATE_FORMATE)
            ///remove(ENV_RESPONSE)
            apply()
        }

    }


}


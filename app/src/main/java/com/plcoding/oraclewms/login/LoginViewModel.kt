package com.plcoding.oraclewms.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.plcoding.oraclewms.BaseApiInterface
import com.plcoding.oraclewms.BuildConfig
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.api.ApiResponse
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.MenuItem
import com.plcoding.oraclewms.home.LandingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

open class LoginViewModel : ViewModel() {

    open var TAG = LoginActivity::class.java.simpleName

    var cmdState: CommandUiState by mutableStateOf(CommandUiState.Empty)
        private set

    var menuItems = arrayListOf<MenuItem>().toMutableStateList()
    var formItems = arrayListOf<FormField>().toMutableStateList()

    fun setState(res: CommandUiState) {
        cmdState = res
        if (res is CommandUiState.Success) {
            formItems.clear()
            menuItems.clear()
            res.response?.menuItems?.let {
                menuItems.addAll(it)
            }
            res.response?.formFields?.let {
                formItems.addAll(it)
            }
        }
    }

    var shellState: ShellUiState by mutableStateOf(ShellUiState.Empty)
        private set

    fun sendCommand(id: String, cmd: String) {
        Log.d(TAG, "Inside sendCommand")
        val obj = JsonObject()
        obj.addProperty("sessionId", id)
        obj.addProperty("command", cmd)
        obj.addProperty("wait_time", 2000)
        cmdState = CommandUiState.Loading
        BaseApiInterface.create()
            .sendCommand(
                BuildConfig.SEND_COMMAND,
                obj
            ).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        val jsonRes = response.body()
                        val gson = Gson()
                        SharedPref.setResponse(gson.toJson(jsonRes?.jsonResponse))
                        SharedPref.setHomeInfo("${jsonRes?.jsonResponse?.env?.value},${jsonRes?.jsonResponse?.appName?.value},${jsonRes?.jsonResponse?.facilityName?.value}")
                        formItems.clear()
                        menuItems.clear()
                        jsonRes?.jsonResponse?.let {
                            menuItems.addAll(it.menuItems)
                            formItems.addAll(it.formFields)
                        }
                        cmdState = CommandUiState.Success(jsonRes?.jsonResponse)
                    } else cmdState = CommandUiState.Error(response.code())
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    cmdState = CommandUiState.Error(HttpURLConnection.HTTP_INTERNAL_ERROR)
                }
            })
    }

    fun startShell(
        id: String,
        env: String,
        email: MutableState<String>,
        password: MutableState<String>
    ) {
        Log.d(TAG, "Inside startShell")
        val obj = JsonObject()
        obj.addProperty("sessionId", id)
        obj.addProperty("environment", env)
        shellState = ShellUiState.Loading

        BaseApiInterface.create()
            .startShell(
                BuildConfig.START_SHELL,
                obj
            ).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        val jsonRes = response.body()
                        shellState = ShellUiState.Success(jsonRes?.jsonResponse)
                        SharedPref.setHomeInfo("${jsonRes?.jsonResponse?.env?.value},${jsonRes?.jsonResponse?.appName?.value},${jsonRes?.jsonResponse?.facilityName?.value}")
                        sendCommand(id, "${email.value.trim()}\t${password.value.trim()}\n")
                    } else {

                        shellState = ShellUiState.Error
                        if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                            sendCommand(id, "${email.value.trim()}\t${password.value.trim()}\n")
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    shellState = ShellUiState.Error
                }
            })
    }

    fun endShell(
        id: String,
        context: Context,
        from: String
    ) {
        Log.d(TAG, "Inside endShell")
        val obj = JsonObject()
        obj.addProperty("sessionId", id)

        BaseApiInterface.create()
            .endShell(
                BuildConfig.END_SHELL,
                obj
            ).enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    //startActivity(context)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    ///startActivity(context)
                }
            })
    }

    fun startActivity(context: Context) {
        SharedPref.deleteAllPref()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        (context as LandingActivity).finish()
    }
}

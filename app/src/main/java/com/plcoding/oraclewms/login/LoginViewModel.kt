package com.plcoding.oraclewms.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.plcoding.oraclewms.BaseApiInterface
import com.plcoding.oraclewms.BuildConfig
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.api.ApiResponse
import com.plcoding.oraclewms.home.LandingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class LoginViewModel : ViewModel() {

    open var TAG = LoginActivity::class.java.simpleName

    var loader: CommandUiState by mutableStateOf(CommandUiState.Empty)

    var cmdState: CommandUiState by mutableStateOf(CommandUiState.Empty)
        private set

    fun setState(res: CommandUiState) {
        cmdState = res
    }

    var shellState: ShellUiState by mutableStateOf(ShellUiState.Empty)
        private set

    fun sendCommand(id: String, cmd: String) {
        Log.d(TAG, "Inside sendCommand")
        val obj = JsonObject()
        obj.addProperty("sessionId", id)
        obj.addProperty("command", cmd)
        cmdState = CommandUiState.Loading
        loader = CommandUiState.Loading
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
                        cmdState = CommandUiState.Success(jsonRes?.jsonResponse)
                        loader = CommandUiState.Success(null)
                    } else {
                        loader = CommandUiState.Error
                        cmdState = CommandUiState.Error
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    loader = CommandUiState.Error
                    cmdState = CommandUiState.Error
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
                        sendCommand(id, "${email.value}\t${password.value}\n")
                    } else {
                        shellState = ShellUiState.Error
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    shellState = ShellUiState.Error
                }
            })
    }

    fun endShell(
        id: String,
        context: Context
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
                    SharedPref.deleteAllPref()
                    startActivity(context)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    SharedPref.deleteAllPref()
                    startActivity(context)
                }
            })
    }

    private fun startActivity(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        (context as LandingActivity).finish()
    }
}

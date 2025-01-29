package com.plcoding.oraclewms.login

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.plcoding.oraclewms.BaseApiInterface
import com.plcoding.oraclewms.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    var cmdState: CommandUiState by mutableStateOf(CommandUiState.Empty)
        private set

    var shellState: ShellUiState by mutableStateOf(ShellUiState.Empty)
        private set

    fun sendCommand(id: String, cmd: String) {
        val obj = JsonObject()
        obj.addProperty("sessionId", id)
        obj.addProperty("command", cmd)
        cmdState = CommandUiState.Loading
        BaseApiInterface.create()
            .sendCommand(
                BuildConfig.SEND_COMMAND,
                obj
            ).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        cmdState = CommandUiState.Success(true)
                    } else {
                        cmdState = CommandUiState.Error
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    cmdState = CommandUiState.Error
                }
            })
    }

    fun startShell(id: String, env: String) {
        val obj = JsonObject()
        obj.addProperty("sessionId", id)
        obj.addProperty("environment", env)
        shellState = ShellUiState.Loading

        BaseApiInterface.create()
            .startShell(
                BuildConfig.START_SHELL,
                obj
            ).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        shellState = ShellUiState.Success(true)
                    } else {
                        shellState = ShellUiState.Error
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    shellState = ShellUiState.Error
                }
            })
    }

}
package com.plcoding.oraclewms.login

import android.content.Context
import android.content.Intent
import android.net.Credentials
import android.util.Base64
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
import com.plcoding.oraclewms.api.Dev
import com.plcoding.oraclewms.api.Env
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.MenuItem
import com.plcoding.oraclewms.api.UserResponse
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

    //'https://tb2.wms.ocs.oraclecloud.com:443/flow_test/wms/lgfapi/v10/entity/user/?auth_user_id__username=jpmars1&null=null&values_list=date_format_id__description'
    fun fetchUserDetails(
        dev: Dev?,
        env: Env,
        email: MutableState<String>,
        name: Boolean,
        password: MutableState<String>
    ) {
        Log.d(TAG, "Inside fetchUserDetails")
        val credentials = "${email.value}:${password.value}"
        BaseApiInterface.create()
            .fetchUserInfo(
                "https://${dev?.host}:443/${env.value}/wms/lgfapi/v10/entity/user/?auth_user_id__username=${email.value}&&values_list=${if(!name) "date_format_id__description" else "auth_user_id__first_name"}",
                "Basic "+Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            ).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    Log.d(TAG, "Inside onResponse")
                    if (response.isSuccessful){
                        val jsonRes = response.body()
                        jsonRes?.results.let {
                            if (it.isNullOrEmpty()) return@let
                            Log.d(TAG, "result "+it.toString())
                            if (name) {
                                SharedPref.setUserName(it.first().auth_user_id__first_name)
                            } else SharedPref.setDateFormat(it.first().date_format_id__description.replace("DD", "dd"))
                        }
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.d(TAG, "Inside onFailure")
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

package com.plcoding.oraclewms.login

import android.content.Context
import android.content.Intent
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
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.LabelResponse
import com.plcoding.oraclewms.api.UserResponse
import com.plcoding.oraclewms.home.LandingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

open class LoginViewModel : ViewModel() {

    open var TAG = LoginViewModel::class.java.simpleName

    var cmdState: CommandUiState by mutableStateOf(CommandUiState.Empty)
        private set

    var loader: Boolean by mutableStateOf(false)
        private set

    var menuItems = arrayListOf<FormField>().toMutableStateList()
    var formItems = arrayListOf<FormField>().toMutableStateList()

    var shipment: String by mutableStateOf("")
        private set

    fun setState(res: CommandUiState) {
        cmdState = res
        if (res is CommandUiState.Success) {
            formItems.clear()
            menuItems.clear()
            val items = ArrayList<FormField>()
            res.response?.menuItems?.let {
                menuItems.addAll(it)
            }

            res.response?.formFields?.let {
                items.addAll(it)
                if (it.isNotEmpty()) res.response.text?.let {
                    items.addAll(it)
                }

            }
            if (items.isNotEmpty()) {
                items.sortBy { it.line_number }
                val index = items.indexOfFirst { it.type.equals("form_field") }
                formItems.addAll(
                    if (index > -1) items.subList(index, items.size)
                    else items
                )
                res.response?.menuItems?.let {
                    formItems.addAll(
                        it
                    )
                }
            }
        }
    }

    var shellState: ShellUiState by mutableStateOf(ShellUiState.Empty)
        private set

    fun sendCommand(id: String, cmd: String, formKey: String? = null) {
        Log.d(TAG, "Inside sendCommand")
        val obj = JsonObject()
        obj.addProperty("sessionId", id)
        obj.addProperty("command", cmd)
        obj.addProperty("wait_time", 2000)
        formKey?.let {
            if (it.equals("Shipment")) {
                shipment = cmd.trim()
                SharedPref.setShipmentID(shipment)
            }


        }

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
                        val items = arrayListOf<FormField>()
                        jsonRes?.jsonResponse?.let {
                            menuItems.addAll(it.menuItems)
                            it.formFields?.let { form ->
                                items.addAll(form)
                                if (form.isNotEmpty()) it.text?.let {
                                    items.addAll(it)
                                }
                            }
                            if (items.isNotEmpty()) {
                                items.sortBy { it.line_number }
                                val index = items.indexOfFirst { it.type.equals("form_field") }
                                val list = arrayListOf<FormField>()
                                list.addAll(
                                    if (index > -1) items.subList(index, items.size)
                                    else items
                                )
                                list.addAll(
                                    it.menuItems
                                )
                                formItems.addAll(list)
                            }
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
                    if (response.isSuccessful) {
                        startActivity(context)
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    startActivity(context)
                }
            })
    }

    //'https://tb2.wms.ocs.oraclecloud.com:443/flow_test/wms/lgfapi/v10/entity/user/?auth_user_id__username=jpmars1&null=null&values_list=date_format_id__description'
    fun fetchUserDetails(
        dev: Dev?,
        env: String?,
        email: String?,
        name: Int,
        password: String?,
        url: String,
        formKey: String? = null
    ) {
        Log.d(TAG, "Inside fetchUserDetails")
        val credentials = "${email}:${password}"
        loader = true
        BaseApiInterface.create()
            .fetchUserInfo(
                "https://${dev?.host}:443/${env}/wms/lgfapi/v10/entity/$url",
                "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            ).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    Log.d(TAG, "Inside onResponse")
                    if (response.isSuccessful) {
                        val jsonRes = response.body()
                        jsonRes?.results.let {
                            if (it.isNullOrEmpty()) return@let
                            Log.d(TAG, "result " + it.toString())
                            if (name == 2) {
                                SharedPref.setUserName(it.first().auth_user_id__first_name)
                            } else if (name == 1) SharedPref.setDateFormat(
                                it.first().date_format_id__description.replace(
                                    "DD",
                                    "dd"
                                )
                            )
                            else {
                                formKey?.let { key ->

                                    fetchLabel(it.first().company_id__code, key)
                                }
                            }
                        }
                    } else
                        loader = false
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    loader = false
                    Log.d(TAG, "Inside onFailure")
                }
            })
    }

    fun fetchLabel(str: String?, formKey: String?) {
        Log.d(TAG, "Inside fetchLabel")
        val obj = JsonObject()
        obj.addProperty("client", str)

        BaseApiInterface.create()
            .fetchLabel(
                BuildConfig.LABEL,
                obj
            ).enqueue(object : Callback<LabelResponse> {
                override fun onResponse(
                    call: Call<LabelResponse>,
                    response: Response<LabelResponse>
                ) {
                    Log.d("label :", response.toString())
                    if (response.isSuccessful) {
                        if (cmdState is CommandUiState.Success) {
                            val form = FormField()
                            form.form_key = formKey
                            (cmdState as CommandUiState.Success).response?.formFields?.let {
                                val index = it.indexOf(form)
                                if (index > -1) {
                                    it[index].flag = false
                                    it[index].form_value = response.body()?.label
                                }
                            }
                        }
                    }
                    loader = false
                }

                override fun onFailure(call: Call<LabelResponse>, t: Throwable) {
                    loader = false
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

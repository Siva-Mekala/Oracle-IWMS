package com.plcoding.oraclewms.login

import InputStreamRequestBody
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.plcoding.oraclewms.BaseApiInterface
import com.plcoding.oraclewms.BuildConfig
import com.plcoding.oraclewms.FilePathUtil
import com.plcoding.oraclewms.SharedPref
import com.plcoding.oraclewms.api.ApiResponse
import com.plcoding.oraclewms.api.Dev
import com.plcoding.oraclewms.api.EnvironmentConfig
import com.plcoding.oraclewms.api.EnvironmentRequest
import com.plcoding.oraclewms.api.FormField
import com.plcoding.oraclewms.api.LabelResponse
import com.plcoding.oraclewms.api.UploadResponse
import com.plcoding.oraclewms.api.User
import com.plcoding.oraclewms.api.UserResponse
import com.plcoding.oraclewms.home.LandingActivity
import com.plcoding.oraclewms.landing.FilterUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.HttpURLConnection

open class LoginViewModel : ViewModel() {

    open var TAG = LoginViewModel::class.java.simpleName

    var filterUIState: FilterUIState by mutableStateOf(FilterUIState.Empty)
        private set

    var cmdState: CommandUiState by mutableStateOf(CommandUiState.Empty)
        private set

    var addEnv: AddEnvState by mutableStateOf(AddEnvState.Empty)
        private set

    fun clearState() {
        addEnv = AddEnvState.Empty
    }

    var loader: Boolean by mutableStateOf(false)
        private set

    var envs = MutableStateFlow<List<Dev>>(emptyList())

    init {
        envs.value = Gson().fromJson(
            SharedPref.getEnvResponse(),
            object : TypeToken<ArrayList<Dev>>() {}.type
        )
    }

    var menuItems = arrayListOf<FormField>().toMutableStateList()
    var formItems = arrayListOf<FormField>().toMutableStateList()
    var images = arrayListOf<Uri>(Uri.parse("")).toMutableStateList()
        private set

    fun addImage(uri: Uri) {
        images.add(uri)
    }

    var shipment: String by mutableStateOf("")
        private set

    private val items = mutableListOf<User>()

    private val _filteredItems = MutableStateFlow(items)
    var filteredItems: StateFlow<List<User>> = _filteredItems

    fun filterText(input: String) {
        items.filter { it.item_id__part_a?.contains(input, ignoreCase = true) == true }.let {
            _filteredItems.value = it.toMutableList()
        }
    }

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
                        0,
                        it
                    )
                }
            }
        }
    }

    var shellState: ShellUiState by mutableStateOf(ShellUiState.Empty)
        private set

    fun sendCommand(id: String, cmd: String, formKey: String? = null, nextField: Boolean? = true) {
        Log.d(TAG, "Inside sendCommand")
        val obj = JsonObject()
        obj.addProperty("sessionId", id)
        obj.addProperty("command", cmd)
        obj.addProperty("wait_time", 2000)
        obj.addProperty("moveCursorToNextField", nextField)
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
                                    0,
                                    it.menuItems
                                )
                                formItems.addAll(0, list)
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

    fun startActivity(context: Context, from: String = "") {
        SharedPref.deleteAllPref()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        if (from.equals("logout")) (context as LandingActivity).finish()
    }

    fun uploadImages(context: Context, quantity: String?) = viewModelScope.launch {
        loader = true
        startWorker(context, quantity).flowOn(Dispatchers.IO).collect {
            if (it == images.size - 1) {
                withContext(Dispatchers.Main) {
                    images.removeRange(1, images.size)
                    loader = false
                }
            }
        }
    }

    fun startWorker(context: Context, quantity: String?) = flow {
        images.let {
            var shipMent: String? = null
            var lpn: String? = null
            var sku: String? = null
            formItems.let {
                it.indexOf(FormField(form_key = "Shipment")).let { index ->
                    if (index > -1)
                        shipMent = it.get(index).form_value
                }
                it.indexOf(FormField(form_key = "LPN")).let { index ->
                    if (index > -1)
                        lpn = it.get(index).form_value
                }
                it.indexOf(FormField(form_key = "SKU")).let { index ->
                    if (index > -1)
                        sku = it.get(index).form_value
                }
            }

            val shipmentId = RequestBody.create(MultipartBody.FORM, shipMent?.trim().toString())
            val bodylpn = RequestBody.create(MultipartBody.FORM, lpn?.trim().toString())
            val bodysku = RequestBody.create(MultipartBody.FORM, sku?.trim().toString())
            val qty = RequestBody.create(MultipartBody.FORM, quantity?.trim().toString())
            val userId = RequestBody.create(MultipartBody.FORM, SharedPref.getLoggedIn())
            val facilityName = RequestBody.create(
                MultipartBody.FORM,
                SharedPref.getHomeInfo()?.split(",")?.get(2).toString()
            )
            val arrayImage = arrayListOf<MultipartBody.Part>()
            repeat(it.size) { index ->
                if (index == 0) return@repeat
                FilePathUtil.getPath(context, it[index]).let { path ->
                    if (path?.isNotEmpty() == true) {
                        path
                    } else {
                        InputStreamRequestBody.getFileName(context, it[index])
                    }
                }?.let {
                    val file = File(it)
                    val requestFile1: RequestBody =
                        RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    arrayImage.add(
                        MultipartBody.Part.createFormData("files", file.getName(), requestFile1)
                    )
                }
            }
            val call: Call<UploadResponse> =
                BaseApiInterface.create()
                    .filesUpload(
                        BuildConfig.UPLOAD,
                        shipmentId,
                        bodylpn,
                        bodysku,
                        qty,
                        userId,
                        facilityName,
                        *arrayImage.toTypedArray()
                    )
            call.execute()
            emit(it.size - 1)
        }
    }

    fun addEnvironment(info: EnvInfo) {
        addEnv = AddEnvState.Loading
        val config = EnvironmentConfig(
            info.host.value,
            info.port.value,
            info.userName.value,
            info.password.value,
            info.description.value
        )
        val req = EnvironmentRequest(info.name.value, config)
        BaseApiInterface.create()
            .addEnvironment(
                BuildConfig.ADD_ENV,
                req
            ).enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    addEnv = if (response.isSuccessful) {
                        val dev = Dev()
                        dev.name = info.name.value
                        dev.host = info.host.value
                        dev.description = info.description.value
                        envs.value += (dev)
                        info.host.value = ""
                        info.name.value = ""
                        info.port.value = ""
                        info.userName.value = ""
                        info.password.value = ""
                        info.description.value = ""
                        val gson = Gson()
                        SharedPref.setEnvResponse(
                            gson.toJson(
                                envs.value,
                                object : TypeToken<ArrayList<Dev>>() {}.type
                            )
                        )
                        AddEnvState.Success("Environment added successfully")
                    } else AddEnvState.Error(response.code())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    addEnv = AddEnvState.Error(HttpURLConnection.HTTP_INTERNAL_ERROR)
                }
            })
    }

    fun removeEnvironment(name: String) {
        addEnv = AddEnvState.Loading
        BaseApiInterface.create()
            .deleteEnvironment(
                BuildConfig.ADD_ENV,
                name
            ).enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    addEnv = if (response.isSuccessful) {
                        val dev = Dev()
                        dev.name = name
                        envs.value -= dev
                        val gson = Gson()
                        SharedPref.setEnvResponse(
                            gson.toJson(
                                envs.value,
                                object : TypeToken<ArrayList<Dev>>() {}.type
                            )
                        )
                        AddEnvState.Success("Environment deleted successfully")
                    } else AddEnvState.Error(response.code())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    addEnv = AddEnvState.Error(HttpURLConnection.HTTP_INTERNAL_ERROR)
                }
            })
    }

    fun fetchShipmentNumber(
        env: String?,
        url: String,
        isNumber: Boolean
    ) {
        filterUIState = FilterUIState.Loading
        val dev =
            Gson().fromJson(SharedPref.getEnv(), Dev::class.java)
        Log.d(TAG, "Inside fetchShipmentNumber")
        val credentials = "${SharedPref.getLoggedIn()}:${SharedPref.getLoggedPwd()}"
        BaseApiInterface.create()
            .fetchShipmentNumber(
                "https://${dev?.host}:443/${env}/wms/lgfapi/v10/entity/$url",
                "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            ).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    Log.d(TAG, "Inside fetchShipmentNumber onResponse")
                    if (response.isSuccessful) {
                        val jsonRes = response.body()
                        jsonRes?.results.let {
                            if (it.isNullOrEmpty()) return@let
                            Log.d(TAG, "result " + it.toString())
                            if (isNumber) fetchShipmentNumber(
                                SharedPref.getEnvValue(),
                                "ib_shipment_dtl/?ib_shipment_id=${it.get(0).id}&values_list=item_id__part_a",
                                false
                            )
                            else {
                                items.clear()
                                items.addAll(it)
                                filterUIState = FilterUIState.Success(it)
                            }
                        }
                    } else filterUIState = FilterUIState.Error(response.code())
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    filterUIState = FilterUIState.Error(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    Log.d(TAG, "Inside fetchShipmentNumber onFailure")
                }
            })
    }
}


package com.plcoding.oraclewms

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.plcoding.oraclewms.api.ApiResponse
import com.plcoding.oraclewms.api.Dev
import com.plcoding.oraclewms.api.EnvironmentRequest
import com.plcoding.oraclewms.api.LabelResponse
import com.plcoding.oraclewms.api.UploadResponse
import com.plcoding.oraclewms.api.UserResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface BaseApiInterface {

    @POST
    fun startShell(
        @Url url: String,
        @Body request: JsonObject
    ): Call<ApiResponse>

    @POST
    fun endShell(
        @Url url: String,
        @Body request: JsonObject
    ): Call<JsonObject>

    @POST
    fun sendCommand(
        @Url url: String,
        @Body request: JsonObject
    ): Call<ApiResponse>

    @GET
    fun fetchUserInfo(
        @Url url: String,
        @Header("Authorization") auth: String
    ): Call<UserResponse>

    @GET
    fun environments(
        @Url url: String
    ): Call<ArrayList<Dev>>

    @POST
    fun fetchLabel(
        @Url url: String,
        @Body request: JsonObject
    ): Call<LabelResponse>

    @Multipart
    @POST
    fun filesUpload(
        @Url url: String,
        @Part("shipment_id")  shipmentId: RequestBody,
        @Part("lpn")  lpn: RequestBody,
        @Part("sku")  sku: RequestBody,
        @Part("qty")  qty: RequestBody,
        @Part("user_id")  userId: RequestBody,
        @Part("facility_name")  facilityName: RequestBody,
        @Part file: MultipartBody.Part,
    ): Call<UploadResponse>

    @POST
    fun addEnvironment(
        @Url url: String,
        @Body request: EnvironmentRequest) : Call<JsonObject>

    @DELETE
    fun deleteEnvironment(
        @Url url: String,
        @Body request: JsonObject) : Call<JsonObject>

    companion object {

        var gson: Gson? = GsonBuilder()
            .setLenient()
            .create()

        private fun getOkHttpClient(): OkHttpClient {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        fun create(): BaseApiInterface {
            val retrofit = getRetrofitObj(BuildConfig.BASE_URL)
            return retrofit.create(BaseApiInterface::class.java)
        }

        fun getRetrofitObj(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .callbackExecutor(Executors.newCachedThreadPool())
                .build()
        }

    }
}

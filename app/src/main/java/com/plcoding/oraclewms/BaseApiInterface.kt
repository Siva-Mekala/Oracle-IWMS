package com.plcoding.oraclewms

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface BaseApiInterface {

    @POST
    fun sendOtp(
        @Url url: String,
        @Body request: JsonObject
    ): Call<String>

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
            val retrofit = getRetrofitObj("https://prod.focused.fun/")
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

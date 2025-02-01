package com.plcoding.oraclewms.splash

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.plcoding.oraclewms.BaseApiInterface
import com.plcoding.oraclewms.BuildConfig
import com.plcoding.oraclewms.api.Dev
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class SplashViewModel : ViewModel() {
    open var TAG = SplashActivityView::class.java.simpleName

    var getEnvState: EnvironmentsUiState by mutableStateOf(EnvironmentsUiState.Empty)
        private set

    fun getEnvironments() {
        Log.d(TAG, "Inside Environments")
        BaseApiInterface.create()
            .environments(
                BuildConfig.ENVIRONMENTS,
            ).enqueue(object : Callback<Map<String, Dev>> {
                override fun onResponse(
                    call: Call<Map<String, Dev>>,
                    response: Response<Map<String, Dev>>
                ) {
                    if (response.isSuccessful) {
                        getEnvState = EnvironmentsUiState.Success(response.body()?.keys?.toList())
                    } else {
                        getEnvState = EnvironmentsUiState.Error
                    }
                }

                override fun onFailure(call: Call<Map<String, Dev>>, t: Throwable) {
                    getEnvState = EnvironmentsUiState.Error
                }
            })
    }
}
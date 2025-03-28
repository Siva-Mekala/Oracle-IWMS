package com.plcoding.oraclewms.login

import com.google.gson.JsonObject

sealed interface AddEnvState {
    data class Success(val response: JsonObject?) : AddEnvState
    data class Error(val code: Int) : AddEnvState
    object Loading : AddEnvState
    object Empty : AddEnvState
}
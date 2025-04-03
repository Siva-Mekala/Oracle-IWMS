package com.plcoding.oraclewms.login

sealed interface AddEnvState {
    data class Success(val response: String?) : AddEnvState
    data class Error(val code: Int) : AddEnvState
    object Loading : AddEnvState
    object Empty : AddEnvState
}
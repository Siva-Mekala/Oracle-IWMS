package com.plcoding.oraclewms.splash


import com.plcoding.oraclewms.api.EnvApiResponse

interface EnvironmentsUiState {
    data class Success(val response: EnvApiResponse?) : EnvironmentsUiState
    object Error : EnvironmentsUiState
    object Loading : EnvironmentsUiState
    object Empty : EnvironmentsUiState
}


package com.plcoding.oraclewms.splash


interface EnvironmentsUiState {
    data class Success(val response: List<String>?) : EnvironmentsUiState
    object Error : EnvironmentsUiState
    object Loading : EnvironmentsUiState
    object Empty : EnvironmentsUiState
}


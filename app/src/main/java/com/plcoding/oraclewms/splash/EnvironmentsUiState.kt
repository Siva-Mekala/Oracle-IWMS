package com.plcoding.oraclewms.splash

import com.plcoding.oraclewms.api.Dev


interface EnvironmentsUiState {
    data class Success(val response: ArrayList<Dev>?) : EnvironmentsUiState
    object Error : EnvironmentsUiState
    object Loading : EnvironmentsUiState
    object Empty : EnvironmentsUiState
}


package com.plcoding.oraclewms.login

import com.plcoding.oraclewms.api.ApiResponse

sealed interface CommandUiState {
    data class Success(val response: ApiResponse?) : CommandUiState
    object Error : CommandUiState
    object Loading : CommandUiState
    object Empty : CommandUiState
}
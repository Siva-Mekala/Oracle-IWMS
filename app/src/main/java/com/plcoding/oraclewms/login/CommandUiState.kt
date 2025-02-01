package com.plcoding.oraclewms.login

import com.plcoding.oraclewms.api.JSONResponse

sealed interface CommandUiState {
    data class Success(val response: JSONResponse?) : CommandUiState
    object Error : CommandUiState
    object Loading : CommandUiState
    object Empty : CommandUiState
}
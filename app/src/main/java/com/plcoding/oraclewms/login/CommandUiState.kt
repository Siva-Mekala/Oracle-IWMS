package com.plcoding.oraclewms.login

import com.plcoding.oraclewms.api.JSONResponse

sealed interface CommandUiState {
    data class Success(val response: JSONResponse?) : CommandUiState
    data class Error (val code : Int) : CommandUiState
    object Loading : CommandUiState
    object Empty : CommandUiState
}
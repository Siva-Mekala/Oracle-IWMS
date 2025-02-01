package com.plcoding.oraclewms.login

import com.plcoding.oraclewms.api.JSONResponse

sealed interface ShellUiState {
    data class Success(val response: JSONResponse?) : ShellUiState
    object Error : ShellUiState
    object Loading : ShellUiState
    object Empty : ShellUiState
}

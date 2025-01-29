package com.plcoding.oraclewms.login

import com.plcoding.oraclewms.api.ApiResponse

sealed interface ShellUiState {
    data class Success(val response: ApiResponse?) : ShellUiState
    object Error : ShellUiState
    object Loading : ShellUiState
    object Empty : ShellUiState
}

package com.plcoding.oraclewms.login

sealed interface ShellUiState {
    data class Success(val response: Boolean) : ShellUiState
    object Error : ShellUiState
    object Loading : ShellUiState
    object Empty : ShellUiState
}

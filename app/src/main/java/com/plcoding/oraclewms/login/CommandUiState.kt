package com.plcoding.oraclewms.login

sealed interface CommandUiState {
    data class Success(val isPaid: Boolean) : CommandUiState
    object Error : CommandUiState
    object Loading : CommandUiState
    object Empty : CommandUiState
}
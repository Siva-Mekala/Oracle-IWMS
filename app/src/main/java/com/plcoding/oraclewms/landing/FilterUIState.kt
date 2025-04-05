package com.plcoding.oraclewms.landing

import com.plcoding.oraclewms.api.User

sealed interface FilterUIState {
    data class Success(val response: List<User>?) : FilterUIState
    data class Error(val code: Int) : FilterUIState
    object Loading : FilterUIState
    object Empty : FilterUIState
}
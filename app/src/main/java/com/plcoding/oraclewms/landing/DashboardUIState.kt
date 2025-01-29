package com.plcoding.focusfun.landing

sealed interface DashboardUIState {
    data class Success(val response: DashboardResponse) : DashboardUIState
    object Error : DashboardUIState
    object Loading : DashboardUIState
    object Empty : DashboardUIState
}
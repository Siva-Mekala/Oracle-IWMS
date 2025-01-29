package com.plcoding.focusfun.landing

sealed class DashBoardScreen(val route: String) {
    object Home : DashBoardScreen("Home")
    object Wallet : DashBoardScreen("Rewards")
}
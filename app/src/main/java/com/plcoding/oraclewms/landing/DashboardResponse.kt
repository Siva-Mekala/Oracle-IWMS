package com.plcoding.focusfun.landing

data class DashboardResponse(
    var current_boost: Double,
    var nft_id: Int,
    var uri: String,
    var name: String,
    var symbol: String,
    var type_of: String
)

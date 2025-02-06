package com.plcoding.oraclewms.api

data class UserResponse(var result_count: Int, var page_count: Int, var page_nbr: Int, var next_page: Any, var prev_page: Any, var results: List<User>)

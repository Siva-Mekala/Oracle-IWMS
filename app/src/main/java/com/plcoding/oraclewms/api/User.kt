package com.plcoding.oraclewms.api

data class User(
    var auth_user_id__first_name: String,
    var date_format_id__description: String,
    var company_id__code: String? = null,
    var id: Long? = null,
    var item_id__part_a: String? = null,
)
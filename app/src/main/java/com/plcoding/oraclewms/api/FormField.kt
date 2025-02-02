package com.plcoding.oraclewms.api

import java.io.Serializable

data class FormField(
    var type: String? = null,
    var value: String? = null,
    var form_key: String? = null,
    var form_value: String? = null,
    var line_number: Int? = null,
    var cursor: Boolean = false,
    var formatters: Formatters? = null
) : Serializable

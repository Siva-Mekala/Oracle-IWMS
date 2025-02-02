package com.plcoding.oraclewms.api

data class FormField(
    var type: String? = null,
    var value: String? = null,
    var form_key: String? = null,
    var form_value: String? = null,
    var line_number: Int? = null,
    var cursor: Boolean = false,
    var formatters: Formatters? = null,
    var bar_code: Boolean = false
) {

    //var cursorState by mutableStateOf(cursor)

}

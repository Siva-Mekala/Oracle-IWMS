package com.plcoding.oraclewms.api

import java.io.Serializable

data class FormField(
    var type: String? = null,
    var value: String? = null,
    var form_key: String? = null,
    var form_value: String? = null,
    var line_number: Int? = null,
    var cursor: Boolean = false,
    var formatters: Formatters? = null,
    var field_formatters: FieldFormatters? = null,
) : Serializable {
    override fun toString(): String {
        return form_key.toString()
    }
}

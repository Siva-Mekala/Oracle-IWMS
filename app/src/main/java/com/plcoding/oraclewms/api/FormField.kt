package com.plcoding.oraclewms.api

import java.io.Serializable
import java.util.Objects

data class FormField(
    var type: String? = null,
    var value: String? = null,
    var form_key: String? = null,
    var form_value: String? = null,
    var line_number: Int? = null,
    var cursor: Boolean = false,
    var formatters: Formatters? = null,
    var field_formatters: FieldFormatters? = null,
    var flag: Boolean? = true,
    var option_number: Int = 1,
    var option_name: String? = null,
) : Serializable {
    override fun toString(): String {
        return form_key.toString()
    }

    override fun hashCode(): Int {
        return Objects.hashCode(form_key)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val form = o as FormField
        return form_key.equals(form.form_key)
    }
}

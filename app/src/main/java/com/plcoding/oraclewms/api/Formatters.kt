package com.plcoding.oraclewms.api

data class Formatters(
    var format_date: Boolean? = null,
    var format_barcode: Boolean? = null,
    var format_label: Boolean? = null,
    var format_search_by_label: Boolean? = null
)

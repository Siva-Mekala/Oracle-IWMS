package com.plcoding.oraclewms.api

data class ShipMentUpload(
    var shipment_id: String?,
    var lpn: String?,
    var sku: String?,
    var qty: String?,
    var user_id: String?,
    var facility_name: String?,
    var images: ArrayList<String>,
    var created_ts: String?,
    var _id: String?
)

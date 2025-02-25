package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FieldFormatters {
    @SerializedName("format_date")
    @Expose
    private Boolean formatDate;
    @SerializedName("format_barcode")
    @Expose
    private Boolean format_barcode;

    public Boolean getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(Boolean formatDate) {
        this.formatDate = formatDate;
    }

    public Boolean getFormat_barcode() {
        return format_barcode;
    }

    public void setFormat_barcode(Boolean format_barcode) {
        this.format_barcode = format_barcode;
    }
}

package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FieldFormatters {
    @SerializedName("format_date")
    @Expose
    private Boolean formatDate;

    public Boolean getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(Boolean formatDate) {
        this.formatDate = formatDate;
    }
}

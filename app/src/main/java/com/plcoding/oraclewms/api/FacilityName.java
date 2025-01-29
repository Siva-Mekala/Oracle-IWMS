package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacilityName {

    @SerializedName("line_number")
    @Expose
    private Integer lineNumber;
    @SerializedName("value")
    @Expose
    private String value;
    public Integer getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

}

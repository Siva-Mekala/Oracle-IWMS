package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Control {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("line_number")
    @Expose
    private Integer lineNumber;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Integer getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

}

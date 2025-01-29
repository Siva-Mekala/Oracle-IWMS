package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Text implements Serializable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("line_number")
    @Expose
    private Integer lineNumber;
    @SerializedName("cursor")
    @Expose
    private Boolean cursor;
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
    public Boolean getCursor() {
        return cursor;
    }
    public void setCursor(Boolean cursor) {
        this.cursor = cursor;
    }
}

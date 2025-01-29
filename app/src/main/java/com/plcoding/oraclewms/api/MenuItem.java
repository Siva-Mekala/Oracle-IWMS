package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuItem {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("option_number")
    @Expose
    private Integer optionNumber;
    @SerializedName("option_name")
    @Expose
    private String optionName;
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
    public Integer getOptionNumber() {
        return optionNumber;
    }
    public void setOptionNumber(Integer optionNumber) {
        this.optionNumber = optionNumber;
    }
    public String getOptionName() {
        return optionName;
    }
    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
    public Integer getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

}

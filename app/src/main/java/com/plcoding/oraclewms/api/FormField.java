package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormField {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("form_key")
    @Expose
    private String formKey;
    @SerializedName("form_value")
    @Expose
    private String formValue;
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
    public String getFormKey() {
        return formKey;
    }
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
    public String getFormValue() {
        return formValue;
    }
    public void setFormValue(String formValue) {
        this.formValue = formValue;
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

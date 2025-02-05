package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Popup implements Serializable {

    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("form_fields")
    @Expose
    private List<FormField> fieldList;

    public Popup(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FormField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FormField> fieldList) {
        this.fieldList = fieldList;
    }
}

package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("app_name")
    @Expose
    private AppName appName;
    @SerializedName("facility_name")
    @Expose
    private FacilityName facilityName;
    @SerializedName("env")
    @Expose
    private Env env;
    @SerializedName("controls")
    @Expose
    private List<Control> controls;
    @SerializedName("text")
    @Expose
    private List<Text> text;
    @SerializedName("form_fields")
    @Expose
    private List<FormField> formFields;
    @SerializedName("menu_items")
    @Expose
    private List<MenuItem> menuItems;
    @SerializedName("popups")
    @Expose
    private List<Popup> popups;
    @SerializedName("cursor_position")
    @Expose
    private CursorPosition cursorPosition;
    public AppName getAppName() {
        return appName;
    }
    public void setAppName(AppName appName) {
        this.appName = appName;
    }
    public FacilityName getFacilityName() {
        return facilityName;
    }
    public void setFacilityName(FacilityName facilityName) {
        this.facilityName = facilityName;
    }
    public Env getEnv() {
        return env;
    }
    public void setEnv(Env env) {
        this.env = env;
    }
    public List<Control> getControls() {
        return controls;
    }
    public void setControls(List<Control> controls) {
        this.controls = controls;
    }
    public List<Text> getText() {
        return text;
    }
    public void setText(List<Text> text) {
        this.text = text;
    }
    public List<FormField> getFormFields() {
        return formFields;
    }
    public void setFormFields(List<FormField> formFields) {
        this.formFields = formFields;
    }
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    public List<Popup> getPopups() {
        return popups;
    }
    public void setPopups(List<Popup> popups) {
        this.popups = popups;
    }
    public CursorPosition getCursorPosition() {
        return cursorPosition;
    }
    public void setCursorPosition(CursorPosition cursorPosition) {
        this.cursorPosition = cursorPosition;
    }
}

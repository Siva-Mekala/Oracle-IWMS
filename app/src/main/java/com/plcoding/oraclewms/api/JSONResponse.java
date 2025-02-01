package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JSONResponse implements Serializable {
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
    private ArrayList<Control> controls;
    @SerializedName("text")
    @Expose
    private ArrayList<Text> text;
    @SerializedName("form_fields")
    @Expose
    private ArrayList<FormField> formFields;
    @SerializedName("menu_items")
    @Expose
    private ArrayList<MenuItem> menuItems;
    @SerializedName("popups")
    @Expose
    private ArrayList<Popup> popups;
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

    public ArrayList<Control> getControls() {
        return controls;
    }

    public void setControls(ArrayList<Control> controls) {
        this.controls = controls;
    }

    public ArrayList<Text> getText() {
        return text;
    }

    public void setText(ArrayList<Text> text) {
        this.text = text;
    }

    public ArrayList<FormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(ArrayList<FormField> formFields) {
        this.formFields = formFields;
    }

    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<Popup> getPopups() {
        return popups;
    }

    public void setPopups(ArrayList<Popup> popups) {
        this.popups = popups;
    }

    public CursorPosition getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(CursorPosition cursorPosition) {
        this.cursorPosition = cursorPosition;
    }
}

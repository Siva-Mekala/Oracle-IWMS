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
    @SerializedName("screen_name")
    @Expose
    private ScreenName screenName;
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
    private ArrayList<FormField> text;
    @SerializedName("form_fields")
    @Expose
    private ArrayList<FormField> formFields;
    @SerializedName("menu_items")
    @Expose
    private ArrayList<FormField> menuItems;
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

    public ArrayList<FormField> getText() {
        return text;
    }

    public void setText(ArrayList<FormField> text) {
        this.text = text;
    }

    public ArrayList<FormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(ArrayList<FormField> formFields) {
        this.formFields = formFields;
    }

    public ArrayList<FormField> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<FormField> menuItems) {
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

    public ScreenName getScreenName() {
        return screenName;
    }

    public void setScreenName(ScreenName screenName) {
        this.screenName = screenName;
    }
}

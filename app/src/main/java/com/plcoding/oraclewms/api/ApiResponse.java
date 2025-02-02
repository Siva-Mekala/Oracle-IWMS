package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiResponse implements Serializable {
    @SerializedName("json_response")
    @Expose
    private JSONResponse jsonResponse;

    public JSONResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JSONResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }
}

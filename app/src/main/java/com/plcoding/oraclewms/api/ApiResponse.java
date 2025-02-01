package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
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

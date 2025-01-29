package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CursorPosition implements Serializable {
    @SerializedName("row")
    @Expose
    private Integer row;
    @SerializedName("col")
    @Expose
    private Integer col;
    public Integer getRow() {
        return row;
    }
    public void setRow(Integer row) {
        this.row = row;
    }
    public Integer getCol() {
        return col;
    }
    public void setCol(Integer col) {
        this.col = col;
    }
}

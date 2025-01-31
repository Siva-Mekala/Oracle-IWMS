package com.plcoding.oraclewms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MenuItem implements Serializable {

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

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int arg1) {
//        dest.writeInt(optionNumber);
//        dest.writeString(optionName);
//        dest.writeInt(lineNumber);
//        dest.writeString(value);
//        dest.writeString(type);
//    }
//
//    public MenuItem(Parcel in) {
//        optionNumber = in.readInt();
//        optionName = in.readString();
//        lineNumber = in.readInt();
//        value = in.readString();
//        type = in.readString();
//    }
//
//    public static final Parcelable.Creator<MenuItem> CREATOR = new Parcelable.Creator<MenuItem>() {
//        public MenuItem createFromParcel(Parcel in) {
//            return new MenuItem(in);
//        }
//
//        public MenuItem[] newArray(int size) {
//            return new MenuItem[size];
//        }
//    };
}

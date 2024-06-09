package com.example.weibo_zhuyufeng;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String meg;

    @SerializedName("data")
    private String data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return meg;
    }

    public String getData() {
        return data;
    }

}

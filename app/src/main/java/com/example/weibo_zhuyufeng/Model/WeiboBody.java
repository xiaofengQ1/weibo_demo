package com.example.weibo_zhuyufeng.Model;

import android.graphics.pdf.PdfDocument;

public class WeiboBody {
    private int code;
    private String meg;
    private WeiboPage data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMeg() {
        return meg;
    }

    public void setMeg(String meg) {
        this.meg = meg;
    }

    public WeiboPage getData() {
        return data;
    }

    public void setData(WeiboPage data) {
        this.data = data;
    }
}

package com.example.weibo_zhuyufeng.Model;

import java.util.List;

public class WeiboPage {
    private List<WeiboInfo> records;
    private int total;
    private int size;
    private int current;
    private int pages;

    public List<WeiboInfo> getRecords() {
        return records;
    }

    public void setRecords(List<WeiboInfo> records) {
        this.records = records;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}

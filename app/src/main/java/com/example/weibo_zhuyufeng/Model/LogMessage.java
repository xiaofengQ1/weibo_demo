package com.example.weibo_zhuyufeng.Model;

public class LogMessage {
    private boolean isLogin;
    public LogMessage(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}

package com.example.weibo_zhuyufeng.Model;

public class LogoutMessage {
    private boolean isLogout;
    public LogoutMessage(boolean isLogin) {
        this.isLogout = isLogin;
    }
    public boolean isLogin() {
        return isLogout;
    }

    public void setLogin(boolean login) {
        isLogout = login;
    }
}

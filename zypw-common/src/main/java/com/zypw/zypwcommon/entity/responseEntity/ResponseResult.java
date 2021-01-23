package com.zypw.zypwcommon.entity.responseEntity;
/**
 * 全局响应实体封装
 * */
public enum ResponseResult {

    SUCCESS(0,"OK!"),
    FAIL(1,"Fail!"),
    LOGIN_ERROR(300,"登录出错!"),
    TOKEN_INVALLID(211,"非法token!"),
    TOKEN_MISSING(212,"token缺失!"),
    USER_MISSING(213,"用户不存在!"),
    TOKEN_EXPIRED(214,"TOKEN过期!请重新登陆");

    private int responseCode;
    private String responseMsg;

    ResponseResult(int responseCode, String responseMsg) {
        this.responseCode = responseCode;
        this.responseMsg = responseMsg;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}

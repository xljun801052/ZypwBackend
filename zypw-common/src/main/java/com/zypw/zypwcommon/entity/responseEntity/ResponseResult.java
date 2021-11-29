package com.zypw.zypwcommon.entity.responseEntity;
/**
 * 自定义：全局响应码和响应码对应提示信息枚举类(参考HttpStatus)
 * */
public enum ResponseResult {

    SUCCESS(200,"success"),
    FAIL(1,"Fail!"),
    LOGIN_ERROR(300,"登录出错!"),
    TOKEN_INVALLID(211,"非法token!"),
    TOKEN_MISSING(212,"token缺失!"),
    USER_MISSING(213,"用户不存在!"),
    TOKEN_EXPIRED(214,"token过期!请重新登陆"),
    TOKEN_NEED_REFRESH(215,"token过期!请进行token更新"),
    LOGOUT_FAIL(216,"退出失败，用户状态异常"),
    LOGOUT_SUCCESS(217,"退出成功");

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

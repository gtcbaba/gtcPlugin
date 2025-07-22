package com.github.gtcbaba.gtcplugin.model.enums;

public enum ErrorCode {

    SUCCESS(0, "ok"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    //NO_VIP_AUTH_ERROR(40102, "无会员权限"),
    ;

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

package com.zhuojian.ct.model;

/**
 * Created by wuhaitao on 2016/3/9.
 */
public class ResponseMsg {

    private String msg;
    private HttpCode code;

    public ResponseMsg(String msg) {
        this.msg = msg;
    }
    public ResponseMsg(HttpCode code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }
    public HttpCode getCode() {
        return code;
    }
    public void setCode(HttpCode code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.zhuojian.ct.model;

/**
 * Created by wuhaitao on 2016/3/9.
 */
public class ResponseMsg<T> {

    private T content;
    private HttpCode code;

    public ResponseMsg(T content) {
        this(HttpCode.OK, content);
    }
    public ResponseMsg(HttpCode code, T content) {
        super();
        this.code = code;
        this.content = content;
    }
    public HttpCode getCode() {
        return code;
    }
    public void setCode(HttpCode code) {
        this.code = code;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}

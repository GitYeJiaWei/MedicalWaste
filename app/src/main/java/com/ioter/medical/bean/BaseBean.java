package com.ioter.medical.bean;

public class BaseBean<T> extends BaseEntity
{
    /**
     * Code : 1001
     * Data : null
     * Message : 新密码和确认密码不一致！
     */

    private int Code;
    private String Message;
    private T Data;

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }
}

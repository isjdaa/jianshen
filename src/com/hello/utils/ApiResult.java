package com.hello.utils;

import com.alibaba.fastjson.JSON;

public class ApiResult {
    private Boolean success;
    private  String message;
    private  Object data;


public static  String json(Boolean success,String message,Object data){
    ApiResult r=new ApiResult();
    r.setData(data);
    r.setMessage(message);
    r.setSuccess(success);
    String json= JSON.toJSONString(r);
    return json;
}

public static String json(Boolean success,String message){
    return json(success,message,null);
}
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}

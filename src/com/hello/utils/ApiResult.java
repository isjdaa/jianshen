package com.hello.utils;

import com.alibaba.fastjson.JSON;

public class ApiResult {
    private Boolean success;
    private String message;
    private Object data;

    // ========== 你原来的所有代码 完全保留 一行不改 ==========
    public static String json(Boolean success, String message, Object data) {
        ApiResult r = new ApiResult();
        r.setData(data);
        r.setMessage(message);
        r.setSuccess(success);
        String json = JSON.toJSONString(r);
        return json;
    }

    public static String json(Boolean success, String message) {
        return json(success, message, null);
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

    // ========== 新增：你预约Servlet中用到的 3个缺失方法 (核心修复) ==========
    // 1. 成功响应 - 链式调用 对应你的 apiResult.success("xxx")
    public ApiResult success(String message) {
        this.setSuccess(true);
        this.setMessage(message);
        this.setData(null);
        return this;
    }

    // 2. 失败响应 - 链式调用 对应你的 apiResult.error("xxx")
    public ApiResult error(String message) {
        this.setSuccess(false);
        this.setMessage(message);
        this.setData(null);
        return this;
    }

    // 3. 转JSON字符串 - 对应你的 apiResult.toJson() 【解决本次报错的核心方法】
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
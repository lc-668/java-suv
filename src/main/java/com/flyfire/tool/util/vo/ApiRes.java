package com.flyfire.tool.util.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApiRes<T> {

    //  返回状态码
    private int code = 200;
    //  返回错误消息
    private String msg;
    //  返回数据
    private T data;

    public static <T> ApiRes<T> newSucces(T data) {
        ApiRes apiResult = new ApiRes();
        apiResult.data = data;
        return apiResult;
    }

    public static <T> ApiRes<T> error(String errMsg) {
        ApiRes apiResult = new ApiRes();
        apiResult.msg = errMsg;
        apiResult.code = 500;
        return apiResult;
    }

    public static <T> ApiRes<T> fail(String errMsg) {
        ApiRes apiResult = new ApiRes();
        apiResult.msg = errMsg;
        apiResult.code = 500;
        return apiResult;
    }

    public static <T> ApiRes<T> fail(int code, String errMsg) {
        ApiRes<T> apiResult = new ApiRes();
        apiResult.msg = errMsg;
        apiResult.code = code;
        return apiResult;
    }

    public boolean success() {
        return code == 200;
    }


    public int getCode() {
        return code;
    }

    /*public String getErrMsg() {
        return msg;
    }*/

    public T getData() {
        return data;
    }

    public ApiException toApiException() {
        return new ApiException(this.getMsg()).setCode(code).setData(data);
    }

}

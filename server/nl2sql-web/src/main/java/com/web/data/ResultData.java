package com.web.data;

import com.web.constant.ReturnCode;
import lombok.Data;

@Data
public class ResultData<T> {
    /** 结果状态 ,具体状态码参见ResultData.java*/
    private int status;
    private String message;
    private T data;
    private long timestamp ;

    public ResultData (){
        this.timestamp = System.currentTimeMillis();
    }

    public  ResultData(ReturnCode returnCode) {
        this();
        this.status = returnCode.getCode();
        this.message = returnCode.getMessage();
    }

    public  ResultData(ReturnCode returnCode, T data) {
        this(returnCode);
        this.data = data;
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC200.getCode());
        resultData.setMessage(ReturnCode.RC200.getMessage());
        resultData.setData(data);
        return resultData;
    }
    public static <T> ResultData<T> success() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC200.getCode());
        resultData.setMessage(ReturnCode.RC200.getMessage());
        return resultData;
    }

    public static <T> ResultData<T> success(ReturnCode returnCode, T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(returnCode.getCode());
        resultData.setMessage(returnCode.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(ReturnCode returnCode) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(returnCode.getCode());
        resultData.setMessage(returnCode.getMessage());
        return resultData;
    }
}
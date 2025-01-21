/*
 *   Copyright (c) 2024 zpei-github
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
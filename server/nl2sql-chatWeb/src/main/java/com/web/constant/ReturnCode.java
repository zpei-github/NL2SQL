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
package com.web.constant;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ReturnCode {

    /**成功**/
    RC200(200,"成功"),

    /**登录和注册**/
    RC4006(4006,"账号或密码错误"),
    RC4007(4007, "账户被禁用"),
    RC4008(4008,"账号被删除，且不能再次注册"),

    RC4009(4009, "用户名已存在"),
    RC4010(4010, "账号或密码不符合要求"),

    RC4011(4011, "用户登录"),
    RC4012(4012, "管理员登录"),

    /**服务异常**/
    RC500(500,"系统异常，请稍后重试"),
    RC5001(5001, "请求的资源不存在"),
    RC5002(5002,"没有权限访问该资源"),
    RC5003(5003,"访问令牌不合法"),
    RC5004(5004,"客户端认证失败"),
    RC5005(5005,"服务异常，请稍后重试"),
    UNSUPPORTED_GRANT_TYPE(1003, "不支持的认证模式");

    /**自定义状态码**/
    private final int code;
    /**自定义描述**/
    private final String message;

    ReturnCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.code + " " + this.message;
    }
}

package com.web.intercepter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.data.ResultData;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * 会拦截全局的Controller返回值
 *
 * */
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    /**
     *
     * */
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }


    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //如果Controller的返回值是字符串，就把改字符串嵌套到ResultData中，最后返回
        if(body instanceof String){
            return objectMapper.writeValueAsString(ResultData.success(body));
        }
        if(body instanceof ResultData){
            return body;
        }
        return ResultData.success(body);
    }
}

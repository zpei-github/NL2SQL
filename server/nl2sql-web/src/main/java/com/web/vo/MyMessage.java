package com.web.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MyMessage {
    private Integer messageId;
    private String sender;
    private String uuid;
    private String content;
    private Long createTime;
}

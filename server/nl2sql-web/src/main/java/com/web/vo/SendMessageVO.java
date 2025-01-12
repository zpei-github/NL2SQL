package com.web.vo;

import lombok.Data;

import java.util.List;

@Data
public class SendMessageVO {
    private MyMessage question;
    private List<MyMessage> existMessages;
}

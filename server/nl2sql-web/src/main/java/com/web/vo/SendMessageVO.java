package com.web.vo;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class SendMessageVO {
    private MyMessage question;
    private LinkedList<MyMessage> existMessages;
}

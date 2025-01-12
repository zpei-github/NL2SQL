package com.web.service;


import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.web.constant.ProjectConstant;
import com.web.vo.MyMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LLMService {
    public MyMessage response(MyMessage question, List<MyMessage> existMessages, String model) throws Exception {
        MyMessage response = new MyMessage();
        List<Message> tongyiMessages = new ArrayList<>();
        for (MyMessage m : existMessages) {
            if (m.getSender().equals(ProjectConstant.USER_SENDER)) {
                tongyiMessages.add(createMessage(Role.USER, m.getContent()));
            }else if (m.getSender().equals(ProjectConstant.CLIENT_SENDER)) {
                tongyiMessages.add(createMessage(Role.ASSISTANT, m.getContent()));
            } else if(m.getSender().equals(ProjectConstant.SYSTEM_SENDER)){
                tongyiMessages.add(createMessage(Role.SYSTEM, m.getContent()));
            }
        }
        tongyiMessages.add(createMessage(Role.USER, question.getContent()));

        GenerationParam param = createGenerationParam(tongyiMessages, model);
        GenerationResult result = callGenerationWithMessages(param);

        response.setMessageId(question.getMessageId() + 1);
        response.setSender(ProjectConstant.CLIENT_SENDER);
        response.setContent(getContent(result));
        response.setCreateTime(System.currentTimeMillis());
        return response;
    }


    public static GenerationParam createGenerationParam(List<Message> messages, String model) {
        return GenerationParam.builder()
                .model(model)
                .messages(messages)
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .build();
    }

    public static GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    public static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }

    public static String getContent(GenerationResult result) {
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

    public static String getResultAsJson(GenerationResult result) {
        return JsonUtils.toJson(result);
    }
}
